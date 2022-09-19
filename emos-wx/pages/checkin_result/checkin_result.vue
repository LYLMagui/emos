<template>
	<view class="page">
		<!-- 签到成功页面第一部分 用户头像昵称-->
		<view class="summary-container">
			<view class="user-info">
				<!-- 显示用户头像和信息-->
				<image :src="photo" mode="widthFix" class="photo"></image>
				<!-- 头像 -->

				<view class="info">
					<!-- 用户信息 -->
					<text class="username">{{ name }}</text>
					<text class="dept">隶属部门：{{ deptName == null ? '' : deptName }}</text>
				</view>
			</view>
			<view class="date">
				<!-- 显示日期 -->
				{{ date }}
			</view>
		</view>

		<!-- 页面第二部分 显示今日签到详情 -->
		<view class="result-container">
			<!-- 左侧图标 -->
			<view class="left">
				<image src="../../static/icon-6.png" mode="widthFix" class="icon-timer"></image>
				<image src="../../static/icon-6.png" mode="widthFix" class="icon-timer"></image>
				<view class="line"></view>
			</view>

			<!-- 右侧内容 -->
			<view class="right">
				<view class="row">
					<text class="start">上班（{{ attendanceTime }}）</text>
				</view>
				<view class="row">
					<text class="checkin-time">签到时间（{{ checkinTime }}）</text>
					<text class="checkin-result green pos" v-if="status == '正常'">{{ status }}</text>
					<text class="checkin-result yellow pos" v-if="status == '迟到'">{{ status }}</text>
				</view>
				<view class="row">
					<image src="../../static/icon-7.png" mode="widthFix" class="icon-small"></image>
					<text class="desc">{{ address }}</text>
					<text class="checkin-result green" v-if="risk == '低风险'">{{ risk }}</text>
					<text class="checkin-result yellow" v-if="risk == '中风险'">{{ risk }}</text>
					<text class="checkin-result red" v-if="risk == '高风险'">{{ risk }}</text>
				</view>
				<view class="row">
					<image src="../../static/icon-8.png" mode="widthFix" class="icon-small"></image>
					<text class="desc">身份验证</text>
					<text class="checkin-result green">已通过</text>
				</view>
				<view class="row">
					<text class="end">下班（{{ closingTime }}）</text>
				</view>
			</view>
		</view>

		<!-- 第三部分 总签到天数和周签到情况 -->
		<view class="checkin-report">
			<image class="big-icon" src="../../static/big-icon-1.png" mode="widthFix"></image>
			<view class="report-title">
				<text class="days">{{ checkinDays }}</text>
				<text class="unit">天</text>
			</view>
			<view class="sub-title">
				<text>累计签到</text>
				<view class="line"></view>
			</view>
			<view class="calendar-container">
				<view class="calendar" v-for="one in weekCheckin" :key="one">
					<image src="../../static/icon-9.png" mode="widthFix" class="calendar-icon" v-if="one.type == '工作日'"></image>
					<image src="../../static/icon-10.png" mode="widthFix" class="calendar-icon" v-if="one.type == '节假日'"></image>
					<text class="day">{{one.day}}</text>
					<text class="result green" v-if="one.status == '正常'">{{one.status}}</text>
					<text class="result yellow" v-if="one.status == '迟到'">{{one.status}}</text>
					<text class="result red" v-if="one.status == '缺勤'">{{one.status}}</text>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			name: '',
			photo: '',
			deptName: '',
			address: '',
			status: '',
			risk: '',
			checkinTime: '', //签到时间
			date: '',
			attendanceTime: '', //上班时间
			closingTime: '', //下班时间
			checkinDays: 0, //签到总天数
			weekCheckin: []
		};
	},
	onShow:function(){
		let that = this;
		that.ajax(that.url.searchTodayCheckin,"GET",null,function(resp){
			let result = resp.data.result;
			console.log(result);
			that.name = result.name;
			that.photo = result.photo;
			that.deptName = result.deptName;
			that.address = result.address;
			that.status = result.status;
			that.risk = result.risk;
			that.checkinTime = result.checkinTime;
			that.date = result.date;
			that.attendanceTime = result.attendanceTime;
			that.closingTime = result.closingTime;
			that.checkinDays = result.checkinDays;
			that.weekCheckin = result.weekCheckin;
		})
	},
	methods: {}
};
</script>

<style lang="less">
@import url('checkin_result');
</style>
