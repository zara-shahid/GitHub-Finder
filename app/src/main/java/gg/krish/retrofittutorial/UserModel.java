package gg.krish.retrofittutorial;

import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("login")        private String login;
    @SerializedName("name")         private String name;
    @SerializedName("bio")          private String bio;
    @SerializedName("avatar_url")   private String avatarUrl;
    @SerializedName("html_url")     private String htmlUrl;
    @SerializedName("followers")    private int followers;
    @SerializedName("following")    private int following;
    @SerializedName("public_repos") private int publicRepos;
    @SerializedName("location")     private String location;
    @SerializedName("company")      private String company;

    public String getLogin()      { return login; }
    public String getName()       { return name; }
    public String getBio()        { return bio; }
    public String getAvatarUrl()  { return avatarUrl; }
    public String getHtmlUrl()    { return htmlUrl; }
    public int    getFollowers()  { return followers; }
    public int    getFollowing()  { return following; }
    public int    getPublicRepos(){ return publicRepos; }
    public String getLocation()   { return location; }
    public String getCompany()    { return company; }
}
