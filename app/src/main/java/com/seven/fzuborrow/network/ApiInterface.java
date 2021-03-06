package com.seven.fzuborrow.network;

import com.seven.fzuborrow.network.response.BasicResponse;
import com.seven.fzuborrow.network.response.CreditRecordResponse;
import com.seven.fzuborrow.network.response.FindAllCommentResponse;
import com.seven.fzuborrow.network.response.FindAllDiscResponse;
import com.seven.fzuborrow.network.response.FindAllGoodsResponse;
import com.seven.fzuborrow.network.response.FindApplyResponse;
import com.seven.fzuborrow.network.response.FindGoodResponse;
import com.seven.fzuborrow.network.response.FindUserResponse;
import com.seven.fzuborrow.network.response.LoginResponse;
import com.seven.fzuborrow.network.response.UploadFileResponse;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @POST("user/register")
    @FormUrlEncoded
    Observable<BasicResponse> register(
            @Field("username") String username,
            @Field("password") String password,
            @Field("schoolid") String schoolid,
            @Field("schoolpw") String schoolpw,
            @Field("address") String address
    );//注册

    @POST("login")
    @FormUrlEncoded
    Observable<LoginResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );//登录

    @POST("user/update")
    @FormUrlEncoded
    Observable<BasicResponse> userInfoUpdate(
            @Header("XW-Token") String token,
            @Field("username") String username,
            @Field("name") String name,
            @Field("department") String department,
            @Field("speciality") String speciality,
            @Field("cls") String clazz,
            @Field("qq") String qq,
            @Field("wechat") String wechat,
            @Field("phonenum") String phonenum
    );//更新用户资料

    @POST("user/update")
    @FormUrlEncoded
    Observable<BasicResponse> userAvatarUpdate(
            @Header("XW-Token") String token,
            @Field("imgurl") String url
    );//换头像

    @POST("user/update")
    @FormUrlEncoded
    Observable<BasicResponse> userPasswordUpdate(
            @Header("XW-Token") String token,
            @Field("password") String password
    );//换头像

    @POST("user/findUser")
    Observable<FindUserResponse> findUser(
            @Header("XW-Token") String token
    );//查询当前用户资料

    @POST("user/getCreditRecord")
    Observable<CreditRecordResponse> getCreditRecord(
            @Header("XW-Token") String token
    );

    @POST("user/findUserByUid")
    @FormUrlEncoded
    Observable<FindUserResponse> findUserByUid(
            @Header("XW-Token") String token,
            @Field("uid") long uid
    );

    @GET("good/findGood")
    Observable<FindGoodResponse> findGood(
            @Header("XW-Token") String token,
            @Query("gid") long goodId
    );//根据id获取物品

    @POST("good/addGood")
    @FormUrlEncoded
    Observable<BasicResponse> addGood(
            @Header("XW-Token") String token,
            @Field("name") String name,
            @Field("type") String type,// Constants
            @Field("detail") String detail,
            @Field("location") String location,
            @Field("price") Double price,
            @Field("imgurl") String imgurl
    );

    @GET("good/findAllGoods")
    Observable<FindAllGoodsResponse> findAllGood(
            @Header("XW-Token") String token,
            @Query("type") String type,
            @Query("keyword") String keyword
    );//根据类别("good"/"room")和关键字获取物品

    @POST("good/findAllGoodsByUid")
    Observable<FindAllGoodsResponse> findAllGoodByUid(
            @Header("XW-Token") String token
    );

    @POST("request/apply")
    @FormUrlEncoded
    Observable<BasicResponse> applyGood(
            @Header("XW-Token") String token,
            @Field("gid") long goodId,
            @Field("pid") long ownerId,
            @Field("reason") String reason,
            @Field("start_time") long startTime,
            @Field("end_time") long endTime
    );//申请租借

    @GET("request/findApply")
    Observable<FindApplyResponse> findApply(
            @Header("XW-Token") String token
    );//获取被申请列表

    @GET("request/findBeApply")
    Observable<FindApplyResponse> findBeApply(
            @Header("XW-Token") String token
    );//获取被申请列表

    @POST("request/handleApply")
    @FormUrlEncoded
    Observable<BasicResponse> handleApply(
            @Header("XW-Token") String token,
            @Field("type") int type,
            @Field("rid") long applyId,
            @Field("gid") long goodId
    );//处理申请，1/0

    @POST("request/returnThing")
    @FormUrlEncoded
    Observable<BasicResponse> returnGood(
            @Header("XW-Token") String token,
            @Field("rid") long applyId,
            @Field("grade") int grade
    );

    @POST("request/confirmReturn")
    @FormUrlEncoded
    Observable<BasicResponse> confirmReturn(
            @Header("XW-Token") String token,
            @Field("rid") long applyId,
            @Field("type") int type,
            @Field("assess") int rate
    );

    @GET("community/findAllDisc")
    Observable<FindAllDiscResponse> findAllDisc(
            @Header("XW-Token") String token,
            @Query("keyword") String keyword
    );//查看所有帖子

    @GET("community/createDisc")
    Observable<BasicResponse> createDisc(
            @Header("XW-Token") String token,
            @Query("title") String title,
            @Query("username") String username,
            @Query("imgurl") String imgurl,
            @Query("grade") int grade
    );

    @GET("community/findAllComment")
    Observable<FindAllCommentResponse> findAllComments(
            @Header("XW-Token") String token,
            @Query("did") long discId
    );//获取帖子的回复

    @POST("community/publCommnet")
    @FormUrlEncoded
    Observable<BasicResponse> publishComment(
            @Header("XW-Token") String token,
            @Field("did") long discId,
            @Field("username") String username,
            @Field("content") String content
    );//发帖子回复

    @GET("community/addDiscLikes")
    Observable<BasicResponse> addDiscLikes(
            @Header("XW-Token") String token,
            @Query("did") long discId
    );//喜欢帖子

    @POST("img/upload")
    @Multipart
    Observable<UploadFileResponse> uploadFile(
            @Header("XW-Token") String token,
            @Part MultipartBody.Part file,
            @Part("type") int type
    );//传文件 1/2/3

    @POST("good/deleteGood")
    @FormUrlEncoded
    Observable<BasicResponse> deleteGood(
            @Header("XW-Token") String token,
            @Field("gid") Long gid
    );//下架


    @POST("community/deleteDisc")
    @FormUrlEncoded
    Observable<BasicResponse> delteDisc(
            @Header("XW-Token") String token,
            @Field("did") Long did
    );//下架



}
