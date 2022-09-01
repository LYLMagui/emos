<template>
	<view>
		<image src="../../static/logo-2.png" mode="widthFix" class="logo"></image>
		<view class="register-container">
			<input type="text" placeholder="输入你的邀请码" class="register-code" maxlength="6" v-model="registerCode" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')" />
			<view class="register-desc">管理员创建员工账号之后，你可以从你的个人邮箱中获得注册邀请码</view>
			<!-- 
				open-type:"getUserInfo" 点击按钮通过 getUserInfo 获取用户信息
			 -->
			<button class="register-btn" open-type="getUserInfo" @tap="register()">执行注册</button>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			
			
		};
	},
	methods: {
		register:function(){
			uni.login({
				//登录服务提供商
				provider:"weixin",
				//成功的回调
				success:function(resp) {
					let code = resp.code;
					//获取用户信息
					uni.getUserInfo({
						provider:"weixin",
						success:function(resp){
							//获得用户微信昵称
							let nicknName = resp.userInfo.nickName;
							//获得用户头像url地址
							let avatarUrl = resp.userInfo.avatarUrl;
							console.log(nicknName);
							console.log(avatarUrl);
						}
					})
				}
			});
		},
	}
};
</script>

<style lang="less">
@import url('register.less');
</style>
