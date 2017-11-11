package com.joshuaneigh.blender.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.joshuaneigh.blender.R;
import com.joshuaneigh.blender.SwiperActivity;
import com.joshuaneigh.blender.model.crypto.Credential;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth login;
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * This method creates the terms and services on the bottom of the app
     *
     * @return Clickable texts that allows the user to see the terms and services
     */
    private ClickableSpan[] generateClickableLegalSpans() {

        ClickableSpan termsOfServicesClick = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
                ds.setFakeBoldText(true);
                ds.setUnderlineText(false);
            }
            @Override
            public void onClick(View view) {
                Log.d("WelcomeFragment", "Terms of Services Clicked");
                Toast.makeText(getContext(), R.string.terms_of_service, Toast.LENGTH_SHORT).show();
            }
        };

        ClickableSpan privacyPolicyClick = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
                ds.setFakeBoldText(true);
                ds.setUnderlineText(false);
            }
            @Override
            public void onClick(View view) {
                Log.d("WelcomeFragment", "Privacy Policy Clicked");
                Toast.makeText(getContext(), R.string.privacy_policy, Toast.LENGTH_SHORT).show();
            }
        };

        return new ClickableSpan[] {termsOfServicesClick, privacyPolicyClick};
    }

    private void createLegalLinks(final View view) {
        final TextView textView = view.findViewById(R.id.login_legal_notice);
        final SpannableString spannableString = new SpannableString(textView.getText());
        final ClickableSpan[] clickableSpans = generateClickableLegalSpans();
        final String[] links = {
                getString(R.string.terms_of_service),
                getString(R.string.privacy_policy)};

        for (int i = 0; i < links.length; i++) {
            ClickableSpan clickableSpan = clickableSpans[i];
            String link = links[i];
            int startIndexOfLink = textView.getText().toString().indexOf(link);
            spannableString.setSpan(clickableSpan, startIndexOfLink, startIndexOfLink + link.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spannableString, TextView.BufferType.SPANNABLE);
        textView.setHighlightColor(Color.TRANSPARENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.view_fragment_login, container, false);
        v.findViewById(R.id.button_login).setOnClickListener(this);
        createLegalLinks(v);
        login = FirebaseAuth.getInstance();
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    /**
     * This is the onClick method that focuses on the button login
     * This uses firebase to login and checks if the email has been verified
     * PostExecute:
     *      If the user successfully logs in then it switches to the swiper activity
     * @param view the fragment listener that checks for the button login
     */
    @Override
    public void onClick(View view) {
        if (mListener != null) {
            if (view.getId() == R.id.button_login) {
                final EditText userE = (EditText) getView().findViewById(R.id.usernameEditText);
                String email = userE.getText().toString();

                final EditText passwordE = (EditText) getView().findViewById(R.id.passwordEditText);
                String password = passwordE.getText().toString();
                if (email.matches("") || email.matches("Email")) {
                    userE.setError(getString(R.string.empty));
                    return;
                }
                if (password.matches("")|| password.matches("Password")) {
                    passwordE.setError(getString(R.string.empty));
                    return;
                }
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password);
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    userE.setError("You did not type the right information, or the email needs to be verified!");
                }
                else if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified() && FirebaseAuth.getInstance().getCurrentUser() != null) {
                    userE.setError("This account must be verified before logging in!");
                    FirebaseAuth.getInstance().signOut();
                }  else {

                    Intent intent = new Intent(this.getContext(), SwiperActivity.class);
                    intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }

                mListener.onFragmentInteraction(Credential.getSerializedKey(email, password));
            }
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(final String key);
    }
}
