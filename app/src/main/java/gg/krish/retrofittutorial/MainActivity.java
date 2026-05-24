package gg.krish.retrofittutorial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private MaterialButton searchButton, openProfileButton;
    private ProgressBar progress;
    private MaterialCardView profileCard;
    private TextView placeholderText, nameText, loginText, bioText, locationText,
            followersCount, followingCount, reposCount;
    private ImageView avatar;

    private final GitHubApi api = GitHubApi.retrofit.create(GitHubApi.class);
    private String currentProfileUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameInput     = findViewById(R.id.username_input);
        searchButton      = findViewById(R.id.search_button);
        openProfileButton = findViewById(R.id.open_profile_button);
        progress          = findViewById(R.id.progress);
        profileCard       = findViewById(R.id.profile_card);
        placeholderText   = findViewById(R.id.placeholder_text);
        nameText          = findViewById(R.id.name_text);
        loginText         = findViewById(R.id.login_text);
        bioText           = findViewById(R.id.bio_text);
        locationText      = findViewById(R.id.location_text);
        followersCount    = findViewById(R.id.followers_count);
        followingCount    = findViewById(R.id.following_count);
        reposCount        = findViewById(R.id.repos_count);
        avatar            = findViewById(R.id.avatar);

        searchButton.setOnClickListener(v -> doSearch());
        usernameInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                doSearch();
                return true;
            }
            return false;
        });
        openProfileButton.setOnClickListener(v -> {
            if (currentProfileUrl != null) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentProfileUrl)));
            }
        });
    }

    private void doSearch() {
        String username = usernameInput.getText() == null ? "" : usernameInput.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, R.string.hint_username, Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyboard();

        progress.setVisibility(View.VISIBLE);
        profileCard.setVisibility(View.GONE);
        placeholderText.setVisibility(View.GONE);

        api.loadUser(username).enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    bind(response.body());
                } else {
                    showError(response.code() == 404
                            ? getString(R.string.error_not_found)
                            : getString(R.string.error_generic));
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                progress.setVisibility(View.GONE);
                showError(getString(R.string.error_generic));
            }
        });
    }

    private void bind(UserModel user) {
        profileCard.setVisibility(View.VISIBLE);

        nameText.setText(TextUtils.isEmpty(user.getName()) ? user.getLogin() : user.getName());
        loginText.setText("@" + user.getLogin());

        if (TextUtils.isEmpty(user.getBio())) {
            bioText.setVisibility(View.GONE);
        } else {
            bioText.setVisibility(View.VISIBLE);
            bioText.setText(user.getBio());
        }

        if (TextUtils.isEmpty(user.getLocation())) {
            locationText.setVisibility(View.GONE);
        } else {
            locationText.setVisibility(View.VISIBLE);
            locationText.setText("📍 " + user.getLocation());
        }

        followersCount.setText(format(user.getFollowers()));
        followingCount.setText(format(user.getFollowing()));
        reposCount.setText(format(user.getPublicRepos()));

        currentProfileUrl = user.getHtmlUrl();

        Glide.with(this)
                .load(user.getAvatarUrl())
                .placeholder(R.drawable.avatar_placeholder)
                .circleCrop()
                .into(avatar);
    }

    private void showError(String msg) {
        placeholderText.setVisibility(View.VISIBLE);
        placeholderText.setText(msg);
        profileCard.setVisibility(View.GONE);
    }

    private String format(int n) {
        if (n >= 1_000_000) return String.format("%.1fM", n / 1_000_000f);
        if (n >= 1_000)     return String.format("%.1fk", n / 1_000f);
        return String.valueOf(n);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
