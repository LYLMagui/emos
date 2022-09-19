<template>
	<view>
		<image src="../../static/logo-2.png" mode="widthFix" class="logo"></image>
		<view class="register-container">
			<input type="text" placeholder="输入6位数字邀请码" class="register-code" maxlength="6" v-model="registerCode" onkeyup="value=value.replace(/[^\w\.\/]/ig,'')" />
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
			registerCode:""

		};
	},
	methods: {
		register:function(){
			//这里的this指代的是vue对象，function中的this指代的是函数对象，为了避免混淆，这里使用that来指代vue对象
			let that = this;

			//表单验证
			if(that.registerCode == null || that.registerCode.length == 0){
				uni.showToast({
					icon:"none",
					title:"邀请码不能为空"
				});
				//结束方法，不执行后面的方法
				return;
			}else if(/^[0-9]{6}$/.test(that.registerCode) == false){//验证邀请码是否为6未数字
				uni.showToast({
					title:"邀请码必须是6位数字",
					icon:"none"
				});
				return;
			}
			
			uni.login({
				//登录服务提供商
				provider:"weixin",
				//成功的回调
				success:function(resp) {
					let code = resp.code;
					console.log(code); 
					//获取用户信息
					uni.getUserInfo({
						provider:"weixin",
						success:function(resp){
							//获得用户微信昵称
							let nicknName = resp.userInfo.nickName;
							//获得用户头像url地址
							let avatarUrl = resp.userInfo.avatarUrl;
							// console.log(nicknName);
							// console.log(avatarUrl);
							/* 发送ajax请求 */
							
							//封装数据
							let data = {
								code:code,
								nickname:nicknName,
								photo:avatarUrl,
								registerCode:that.registerCode
							}
							//调用ajax请求
							that.ajax(that.url.register,"POST",data,function(resp){								
								//获取用户权限列表
								let permission = resp.data.permission;
								//将用户权限列表存储到Storage内
								uni.setStorageSync("permission",permission);
								console.log(permission);
								// 跳转到index页面
								uni.switchTab({
									url:'../index/index'
								});
							})
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
