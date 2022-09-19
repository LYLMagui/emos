<template>
	<view>
		<!-- 
			mode:widthFix 锁定宽高比例
		 -->
		<image src="../../static/logo-1.png" class="logo" mode="widthFix"></image>
		<view class="logo-title">EMOS企业在线办公系统</view>
		<view class="logo-subtitle">Ver 2022.1</view>
		<button class="login-btn" open-type="getUserInfo" @tap="login()">登录系统</button>
		<view class="register-container">
			没有账号？
			<text class="register" @tap="toRegister()">立即注册</text>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {};
	},
	methods: {
		//跳转注册页面
		toRegister: function() {
			uni.navigateTo({
				url: '../register/register'
			});
		},
		
		//登录功能
		login:function(){
			let that = this;
			
			uni.login({
				provider:"weixin",
				//成功的回调
				success:function(resp){
					let code = resp.code;
					console.log("临时授权字符串 "+code);
					that.ajax(that.url.login,"POST",{"code":code},function(resp){
						//获取权限列表
						let permission = resp.data.permission;
						//存储到storage变量里
						uni.setStorageSync("permission",permission);
						//跳转到index页面
						uni.switchTab({
							url:'../index/index'
						});
					})
					console.log("success");
					

				},
				//失败的回调
				fail:function(e){
					console.log(e);
					uni.showToast({
						icon:"none",
						title:"执行异常"
					})
				}
			})
		}
	}
};
</script>

<style lang="less">
@import url('login.less');
</style>
