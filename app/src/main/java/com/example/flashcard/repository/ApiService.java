package com.example.flashcard.repository;

import com.example.flashcard.model.folder.AddTopicToFolderResponse;
import com.example.flashcard.model.folder.DeleteFolder;
import com.example.flashcard.model.folder.DeleteFolderDetail;
import com.example.flashcard.model.folder.FolderResponse;
import com.example.flashcard.model.folder.FoldersFormUserResponse;
import com.example.flashcard.model.topic.AddTopicToFolder;
import com.example.flashcard.model.topic.DeleteTopic;
import com.example.flashcard.model.topic.PublicTopicResponse;
import com.example.flashcard.model.topic.TopicDetailResponse;
import com.example.flashcard.model.topic.TopicFromFolderResponse;
import com.example.flashcard.model.topic.TopicResponse;
import com.example.flashcard.model.topic.TopicsFormUserResponse;
import com.example.flashcard.model.topic.UpdateTopicResponse;
import com.example.flashcard.model.user.LoginResponse;
import com.example.flashcard.model.user.RegisterResponse;
import com.example.flashcard.model.user.UpdateResponse;
import com.example.flashcard.model.user.UserFromTopicResponse;
import com.example.flashcard.model.vocabulary.DeleteVocabularyResponse;
import com.example.flashcard.model.vocabulary.VocabulariesFromTopicResponse;
import com.example.flashcard.model.vocabulary.VocabularyResponse;
import com.example.flashcard.utils.UnsplashResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("register.php")
    Call<RegisterResponse> register(@Field("username") String username, @Field("password") String password, @Field("email") String email, @Field("age") int age);

    @FormUrlEncoded
    @POST("updateUser.php")
    Call<UpdateResponse> updateUserProfile(
            @Field("userID") int userID,
            @Field("newEmail") String newEmail,
            @Field("newProfileImage") String newProfileImage,
            @Field("newPassword") String newPassword,
            @Field("newAge") Integer newAge
    );

    @FormUrlEncoded
    @POST("createTopic.php")
    Call<TopicResponse> CreateTopic(
            @Field("topicName") String topicName,
            @Field("description") String description,
            @Field("isPublic") int isPublic,
            @Field("ownerID") int ownerID
    );

    @FormUrlEncoded
    @POST("insertTopicDetail.php")
    Call<TopicDetailResponse> insertTopicDetail(
            @Field("topicID") int topicID,
            @Field("userID") int userID
    );

    @FormUrlEncoded
    @POST("createVocabulary.php")
    Call<VocabularyResponse> createVocabulary(
            @Field("vocabulary") String vocabulary,
            @Field("meaning") String meaning,
            @Field("topicID") int topicID
    );

    @FormUrlEncoded
    @POST("createFolder.php")
    Call<FolderResponse> createFolder(
            @Field("userID") int userID,
            @Field("folderName") String folderName,
            @Field("folderDescription") String folderDescription
    );

    @FormUrlEncoded
    @POST("get_topic_user.php")
    Call<TopicsFormUserResponse> getUserTopic(
            @Field("userID") int userID
    );

    @FormUrlEncoded
    @POST("get_folder_user.php")
    Call<FoldersFormUserResponse> getUserFolder(
            @Field("userID") int userID
    );

    @FormUrlEncoded
    @POST("getVocabularyByTopic.php")
    Call<VocabulariesFromTopicResponse> getVocabulariesFromTopic(
            @Field("topicID") int topicID
    );

    @FormUrlEncoded
    @POST("getUserByTopicID.php")
    Call<UserFromTopicResponse> getUserFromTopic(
            @Field("topicID") int topicID
    );

    @FormUrlEncoded
    @POST("findPubluTopic.php")
    Call<PublicTopicResponse> getPublicTopic(
            @Field("userID") int userId

    );

    @FormUrlEncoded
    @POST("updateTopic.php")
    Call<UpdateTopicResponse> updateTopic(
            @Field("topicID") int topicID,
            @Field("topicName") String topicName,
            @Field("description") String description,
            @Field("isPublic") int isPublic,
            @Field("isPublic") int ownerID
    );

    @FormUrlEncoded
    @POST("deleteVocabulary.php")
    Call<DeleteVocabularyResponse> deleteVocabulary(
            @Field("topicID") int topicID
    );

    @FormUrlEncoded
    @POST("deleteTopic.php")
    Call<DeleteTopic> deleteTopic(
            @Field("topicid") int topicid
    );

    @FormUrlEncoded
    @POST("insertFolderDetail.php")
    Call<AddTopicToFolderResponse> insertFolderDetail(
            @Field("folderID") int folderID,
            @Field("topicID") int topicID
    );

    @FormUrlEncoded
    @POST("get_Topic_Folder.php")
    Call<TopicFromFolderResponse> selectTopicFolder(
            @Field("folderID") int folderID
    );

    @FormUrlEncoded
    @POST("deleteFolder.php")
    Call<DeleteFolder> deleteFolder(
            @Field("folderID") int folderID
    );

    @FormUrlEncoded
    @POST("deleteFolderDetail.php")
    Call<DeleteFolderDetail> deleteFolderDetail(
            @Field("folderID") int folderID
    );

    @FormUrlEncoded
    @POST("topic_not_exist_folder.php")
    Call<AddTopicToFolder> selectTopicToFolder(
            @Field("folderID") int folderID,
            @Field("userID") int userID

    );
}
