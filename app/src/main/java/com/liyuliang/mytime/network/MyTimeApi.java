package com.liyuliang.mytime.network;

import com.google.gson.JsonObject;
import com.liyuliang.mytime.bean.Result;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Retrofit网络请求构建接口
 * Created at 2018/11/28 13:48
 *
 * @author LiYuliang
 * @version 1.0
 */

public interface MyTimeApi {

    /**
     * 查询服务器RSA公钥
     *
     * @return 返回值
     */
    @GET("user/rsaPublicKey.do")
    Observable<Result> getRSAPublicKey();

    /**
     * 查询首页轮播图
     *
     * @return 返回值
     */
    @POST("user/searchHomePagePicture.do")
    Observable<Result> searchHomePagePicture(@Body JsonObject params);

    /**
     * 注册
     *
     * @param params 参数
     * @return 返回值
     */
    @POST("user/register.do")
    Observable<Result> register(@Body JsonObject params);

    /**
     * 登录
     *
     * @param params 参数
     * @return 返回值
     */
    @POST("user/login.do")
    Observable<Result> login(@Body JsonObject params);

    /**
     * 根据userId查询用户
     *
     * @param params 参数
     * @return 返回值
     */
    @POST("user/searchUser.do")
    Observable<Result> searchUser(@Body JsonObject params);

    /**
     * 更新主账号密码
     *
     * @param params 参数
     * @return 返回值
     */
    @POST("user/updatePassword.do")
    Observable<Result> updatePassword(@Body JsonObject params);

    /**
     * 重置（找回）主账号密码
     *
     * @param params 参数
     * @return 返回值
     */
    @POST("user/resetPassword.do")
    Observable<Result> resetPassword(@Body JsonObject params);

    /**
     * 更新用户信息
     *
     * @param params 参数
     * @return 返回值
     */
    @POST("user/updateInfo.do")
    Observable<Result> updateInfo(@Body JsonObject params);

    /**
     * 上传人脸信息
     *
     * @return 返回值
     */
    @Multipart
    @POST("user/addFace.do")
    Observable<Result> addFace(@Part("information") RequestBody information, @Part MultipartBody.Part file);

    /**
     * 查询人脸信息
     *
     * @param params 参数
     * @return 返回值
     */
    @POST("user/searchFace.do")
    Observable<Result> searchFace(@Body JsonObject params);

    /**
     * 更新部门与职位信息
     *
     * @param params 参数
     * @return 返回值
     */
    @POST("user/updateDepartment.do")
    Observable<Result> updateDepartment(@Body JsonObject params);

    /**
     * 更新紧急联系人信息
     *
     * @param params 参数
     * @return 返回值
     */
    @POST("user/updateEmergencyContact.do")
    Observable<Result> updateEmergencyContact(@Body JsonObject params);

    /**
     * 查找部门信息
     *
     * @return 返回值
     */
    @POST("user/searchDepartment.do")
    Observable<Result> searchDepartment(@Body JsonObject params);

    /**
     * 查找所有用户
     *
     * @return 返回值
     */
    @POST("user/searchAllUser.do")
    Observable<Result> searchAllUser(@Body JsonObject params);

    /**
     * 查询考勤规则
     *
     * @return 返回值
     */
    @POST("user/searchAttendanceRules.do")
    Observable<Result> searchAttendanceRules(@Body JsonObject params);

    /**
     * 删除考勤规则
     *
     * @return 返回值
     */
    @POST("user/deleteAttendanceRule.do")
    Observable<Result> deleteAttendanceRule(@Body JsonObject params);

    /**
     * 添加考勤记录
     *
     * @return 返回值
     */
    @Multipart
    @POST("user/addAttendanceRecord.do")
    Observable<Result> addAttendanceRecord(@Part("information") RequestBody information, @Part List<MultipartBody.Part> files);

    /**
     * 查询考勤记录
     *
     * @return 返回值
     */
    @POST("user/searchAttendanceRecord.do")
    Observable<Result> searchAttendanceRecord(@Body JsonObject params);

    /**
     * 查询考勤规则和考勤记录
     *
     * @return 返回值
     */
    @POST("user/searchAttendanceRulesAndRecord.do")
    Observable<Result> searchAttendanceRulesAndRecord(@Body JsonObject params);

    /**
     * 查询是否是假期
     *
     * @return 返回值
     */
    @POST("user/searchHoliday.do")
    Observable<Result> searchHoliday(@Body JsonObject params);

    /**
     * 更新用户头像
     *
     * @param information 描述信息
     * @param file        头像文件
     * @return 更新结果
     */
    @Multipart
    @POST("user/uploadHeadPortrait.do")
    Observable<Result> uploadUserIcon(@Part("information") RequestBody information, @Part MultipartBody.Part file);

    /**
     * 软件历史版本更新日志信息
     *
     * @return 返回值
     */
    @POST("user/getNewVersionUpdateLog.do")
    Observable<Result> getVersionLog(@Body JsonObject params);

    /**
     * 上传错误日志文件
     *
     * @param file 错误日志文件
     * @return 上传结果
     */
    @Multipart
    @POST("AndroidController/uploadAndroidLog.do")
    Observable<Result> uploadCrashFiles(@Part MultipartBody.Part file);

    /**
     * 下载软件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    @GET
    Call<ResponseBody> downloadFile(@Url String filePath);

    /**
     * 查询假期类型
     *
     * @return 返回值
     */
    @POST("user/searchVacationTypes.do")
    Observable<Result> searchVacationTypes(@Body JsonObject params);

    /**
     * 查询用户有多少可调休时间
     *
     * @return 返回值
     */
    @POST("user/searchUserOvertimeTime.do")
    Observable<Result> searchUserOvertimeTime(@Body JsonObject params);

    /**
     * 查询加班类型
     *
     * @return 返回值
     */
    @POST("user/searchOvertimeTypes.do")
    Observable<Result> searchOvertimeTypes(@Body JsonObject params);

    /**
     * 查询工资
     *
     * @return 返回值
     */
    @POST("user/searchWageByUserId.do")
    Observable<Result> searchWageByUserId(@Body JsonObject params);

    /**
     * 查询通知公告
     *
     * @return 返回值
     */
    @POST("user/searchAnnouncement.do")
    Observable<Result> searchAnnouncement(@Body JsonObject params);

    /**
     * 管理员查询外勤审批
     *
     * @return 返回值
     */
    @POST("user/searchOutAttendance.do")
    Observable<Result> searchOutAttendance(@Body JsonObject params);

    /**
     * 管理员提交外勤审批
     *
     * @return 返回值
     */
    @POST("user/submitApprovalOutAttendance.do")
    Observable<Result> submitApprovalOutAttendance(@Body JsonObject params);

    /**
     * 用户查询外勤审批申请记录
     *
     * @return 返回值
     */
    @POST("user/searchApprovalAttendanceRecord.do")
    Observable<Result> searchApprovalOutAttendanceRecord(@Body JsonObject params);

    /**
     * 添加考勤补卡申请
     *
     * @return 返回值
     */
    @Multipart
    @POST("user/addAppealAttendanceRecord.do")
    Observable<Result> addAppealAttendanceRecord(@Part("information") RequestBody information, @Part List<MultipartBody.Part> files);

    /**
     * 管理员查询补卡申请
     *
     * @return 返回值
     */
    @POST("user/searchAppealAttendance.do")
    Observable<Result> searchAppealAttendance(@Body JsonObject params);

    /**
     * 申请人撤销外勤打卡申请
     *
     * @return 返回值
     */
    @POST("user/submitRevokeOutAttendance.do")
    Observable<Result> submitRevokeOutAttendance(@Body JsonObject params);

    /**
     * 申请人撤销考勤补卡申请
     *
     * @return 返回值
     */
    @POST("user/submitRevokeAppealAttendance.do")
    Observable<Result> submitRevokeAppealAttendance(@Body JsonObject params);

    /**
     * 用户查询考勤补卡申请记录
     *
     * @return 返回值
     */
    @POST("user/searchApprovalAppealAttendanceRecord.do")
    Observable<Result> searchApprovalAppealAttendanceRecord(@Body JsonObject params);

    /**
     * 管理员提交考勤补卡审批
     *
     * @return 返回值
     */
    @POST("user/submitApprovalAppealAttendance.do")
    Observable<Result> submitApprovalAppealAttendance(@Body JsonObject params);

    /**
     * 添加加班申请
     *
     * @return 返回值
     */
    @Multipart
    @POST("user/addOvertimeRecord.do")
    Observable<Result> addOvertimeRecord(@Part("information") RequestBody information, @Part List<MultipartBody.Part> files);

    /**
     * 用户查询加班申请记录
     *
     * @return 返回值
     */
    @POST("user/searchApprovalOvertime.do")
    Observable<Result> searchApprovalOvertime(@Body JsonObject params);

    /**
     * 申请人撤销考加班申请
     *
     * @return 返回值
     */
    @POST("user/submitRevokeOverTime.do")
    Observable<Result> submitRevokeOverTime(@Body JsonObject params);

    /**
     * 管理员查询加班申请
     *
     * @return 返回值
     */
    @POST("user/searchOvertime.do")
    Observable<Result> searchOvertime(@Body JsonObject params);

    /**
     * 管理员提交加班申请的审批
     *
     * @return 返回值
     */
    @POST("user/submitApprovalOvertime.do")
    Observable<Result> submitApprovalOvertime(@Body JsonObject params);

    /**
     * 添加休假申请
     *
     * @return 返回值
     */
    @Multipart
    @POST("user/addVacationRecord.do")
    Observable<Result> addVacationRecord(@Part("information") RequestBody information, @Part List<MultipartBody.Part> files);

    /**
     * 用户查询休假申请记录
     *
     * @return 返回值
     */
    @POST("user/searchApprovalVacation.do")
    Observable<Result> searchApprovalVacation(@Body JsonObject params);

    /**
     * 申请人撤销休假申请
     *
     * @return 返回值
     */
    @POST("user/submitRevokeVacation.do")
    Observable<Result> submitRevokeVacation(@Body JsonObject params);

    /**
     * 管理员查询休假申请
     *
     * @return 返回值
     */
    @POST("user/searchVacation.do")
    Observable<Result> searchVacation(@Body JsonObject params);

    /**
     * 管理员提交休假申请的审批
     *
     * @return 返回值
     */
    @POST("user/submitApprovalVacation.do")
    Observable<Result> submitApprovalVacation(@Body JsonObject params);

    /**
     * 添加外出申请
     *
     * @return 返回值
     */
    @Multipart
    @POST("user/addOutGoingRecord.do")
    Observable<Result> addOutGoingRecord(@Part("information") RequestBody information, @Part List<MultipartBody.Part> files);

    /**
     * 用户查询外出申请记录
     *
     * @return 返回值
     */
    @POST("user/searchApprovalOutGoing.do")
    Observable<Result> searchApprovalOutGoing(@Body JsonObject params);

    /**
     * 申请人撤销外出申请
     *
     * @return 返回值
     */
    @POST("user/submitRevokeOutGoing.do")
    Observable<Result> submitRevokeOutGoing(@Body JsonObject params);

    /**
     * 管理员查询外出申请
     *
     * @return 返回值
     */
    @POST("user/searchOutGoing.do")
    Observable<Result> searchOutGoing(@Body JsonObject params);

    /**
     * 管理员提交外出申请的审批
     *
     * @return 返回值
     */
    @POST("user/submitApprovalOutGoing.do")
    Observable<Result> submitApprovalOutGoing(@Body JsonObject params);

    /**
     * 添加出差申请
     *
     * @return 返回值
     */
    @Multipart
    @POST("user/addBusinessTraveIRecord.do")
    Observable<Result> addBusinessTravelRecord(@Part("information") RequestBody information, @Part List<MultipartBody.Part> files);

    /**
     * 管理员查询出差申请记录
     *
     * @return 返回值
     */
    @POST("user/searchBusinessTraveI.do")
    Observable<Result> searchBusinessTravel(@Body JsonObject params);

    /**
     * 申请人撤销出差申请
     *
     * @return 返回值
     */
    @POST("user/submitRevokeBusinessTraveI.do")
    Observable<Result> submitRevokeBusinessTravel(@Body JsonObject params);

    /**
     * 用户查询出差申请记录
     *
     * @return 返回值
     */
    @POST("user/searchApprovalBusinessTraveI.do")
    Observable<Result> searchApprovalBusinessTravel(@Body JsonObject params);

    /**
     * 管理员提交出差申请的审批
     *
     * @return 返回值
     */
    @POST("user/submitApprovalBusinessTraveI.do")
    Observable<Result> submitApprovalBusinessTravel(@Body JsonObject params);

    /**
     * 极光推送后，查询申请记录详情
     *
     * @return 返回值
     */
    @POST("user/searchRecordDetails.do")
    Observable<Result> searchRecordDetails(@Body JsonObject params);

    /**
     * 极光推送后，查询申请记录详情
     *
     * @return 返回值
     */
    @POST("user/getEmployeeStatus.do")
    Observable<Result> getEmployeeStatus(@Body JsonObject params);

    /**
     * 查询人脸识别记录
     *
     * @return 返回值
     */
    @POST("user/searchFaceRecord.do")
    Observable<Result> searchFaceRecord(@Body JsonObject params);

    /**
     * 查询最新的版本信息
     *
     * @return 返回值
     */
    @POST("user/searchNewVersion.do")
    Observable<Result> searchNewVersion(@Body JsonObject params);

    /**
     * 下载软件
     *
     * @return 文件
     */
    @Streaming
    @GET
    Observable<ResponseBody> executeDownload(@Header("Range") String range, @Url() String url);

}
