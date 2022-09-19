<template>
	<view>
		<!-- 
			相机取景框 具体参数请查阅：https://uniapp.dcloud.net.cn/component/camera.html
			device-position: front/back 前置/后置摄像头
			flash:off  					闪光灯关闭
			@error: error 				用户不允许使用摄像头时触发的方法
 		 -->
		<camera device-position="front" flash="off" class="camera" @error="error" v-if="showCamera"></camera>
		<!-- 照片展示框 -->
		<image mode="widthFix" class="image" :src="photoPath" v-if="showImage"></image>
		<!-- 按钮 -->
		<view class="operate-container">
			<button type="primary" class="btn" @tap="clickBtn" :disabled="!canCheckin">{{ btnText }}</button>
			<button type="warn" class="btn" @tap="afresh" :disabled="!canCheckin">重拍</button>
		</view>
		<view class="notice-container">
			<text class="notice">注意事项</text>
			<text class="desc">拍照签到的时候，必须要拍摄自己的正面照片，侧面照片会导致无法识别。另外，拍照的时候不要戴墨镜或者帽子，避免影响拍照签到的准确度。</text>
		</view>
	</view>
</template>

<script>
//引入位置服务的sdk
var QQMapWX = require('../../lib/qqmap-wx-jssdk.min.js');

//用于初始化
var qqmapsdk;

export default {
	data() {
		return {
			//
			canCheckin: true, //是否可以签到
			photoPath: '', //存储照片路径
			btnText: '拍照',
			showCamera: true, //默认显示取景框
			showImage: false //默认不显示展示照片框
		};
	},
	onLoad: function() {
		qqmapsdk = new QQMapWX({
			//填写自己的key
			key: 'BANBZ-MSCLW-QWMRB-R6GYB-4QJ6V-J3FC7'
		});
	},
	onShow: function() {
		let that = this;
		that.ajax(that.url.validCanCheckIn, 'GET', null, function(resp) {
			let msg = resp.data.msg;
			if (msg != '可以考勤') {
				console.log(msg)
				that.canCheckin = false;
				uni.showModal({
					title: '温馨提示',
					content: msg,
					showCancel: false, //不显示取消按钮
					success: function() {}
				});
			}
		});
	},
	methods: {
		//拍照功能
		clickBtn: function() {
			let that = this;
			//判断点击的按钮是不是拍照按钮
			if (that.btnText == '拍照') {
				//调用摄像头对象
				let ctx = uni.createCameraContext();
				ctx.takePhoto({
					quality: 'high', // 拍照质量
					success: function(resp) {
						// 成功的回调
						console.log(resp.tempImagePath);
						that.photoPath = resp.tempImagePath; //存储图片地址
						that.showCamera = false; //隐藏拍照框
						that.showImage = true; //显示照片框
						that.btnText = '签到'; //更改按钮名字
					}
				});
			} else {
				//执行签到功能

				//签到过程比较耗时，所以需要用户等待
				uni.showLoading({
					title: '签到中请稍后'
				});
				//30秒后隐藏加载层
				setTimeout(function() {
					uni.hideLoading();
				}, 10000);

				//获取位置
				uni.getLocation({
					type: 'wgs84', //返回类型，gps坐标
					success: function(resp) {
						//成功的回调
						//获取经纬度
						let latitude = resp.latitude;
						let longitude = resp.longitude;
						console.log('当前位置经度为：' + latitude);
						console.log('当前位置纬度为：' + longitude);

						//调用sdk的方法获取具体位置信息
						qqmapsdk.reverseGeocoder({
							location: {
								latitude: latitude,
								longitude: longitude
							},
							//拍照成功后的回调
							success: function(resp) {
								console.log(resp.result);
								//从响应的结果中获取位置信息
								let address = resp.result.address;

								let addressComponent = resp.result.address_component;
								//获取国家名字
								let nation = addressComponent.nation;
								//获取省份名字
								let province = addressComponent.province;
								//获取城市名字
								let city = addressComponent.city;
								//获取区域名字
								let district = addressComponent.district;

								//上传图片
								uni.uploadFile({
									url: that.url.checkin, //请求路径
									filePath: that.photoPath, //文件路径
									name: 'photo', //上传时的名字
									header: {
										token: uni.getStorageSync('token')
									},
									formData: {
										//请求体
										address: address,
										country: nation,
										province: province,
										city: city,
										district: district
									},
									success: function(resp) {
										if (resp.statusCode == 500 && resp.data == '不存在人脸模型') {
											uni.hideLoading();
											uni.showModal({
												titl: '提示信息',
												content: '系统中不存在你的人脸数据，是否用当前这张照片作为人脸数据？',
												success: function(res) {
													if (res.confirm) {
														//用户点击了确认按钮
														uni.uploadFile({
															url: that.url.createFaceModel, //请求路径
															filePath: that.photoPath, //文件路径
															name: 'photo', //上传时的名字
															header: {
																token: uni.getStorageSync('token')
															},
															success: function(resp) {
																if (resp.statusCode == 500) {
																	//创建人脸失败
																	uni.showToast({
																		title: resp.data,
																		icon: 'none',
																		duration: 8000
																	});
																} else if (resp.statusCode == 200) {
																	uni.showToast({
																		title: '人脸数据已录入',
																		icon: 'none'
																	});
																}
															}
														});
													}
												}
											});
										} else if (resp.statusCode == 200) {
											//签到成功的状态码
											let data = JSON.parse(resp.data);
											//获取业务状态码
											let code = data.code;
											let msg = data.msg;
											if (code == 200) {
												//隐藏加载图标
												uni.hideLoading();
												uni.showToast({
													title: '签到成功',
													// 跳转到考勤统计页面
													complete: function() {
														uni.navigateTo({
															url: '../checkin_result/checkin_result'
														});
													}
												});
											}
										} else if (resp.statusCode == 500) {
											//其他异常的显示
											// uni.showToast({
											// 	title: resp.data,
											// 	icon: 'none'
											// });
											uni.showModal({
												title: '温馨提示',
												content: msg,
												showCancel: false, //不显示取消按钮
												success: function() {}
											});
										}
									},
									fail: function() {
										console.log('上传失败');
									}
								});
							}
						});
					},
					fail: function() {
						console.log('获取经纬度失败');
					}
				});
			}
		},
		//重拍功能
		afresh: function() {
			let that = this;
			that.showCamera = true;
			that.showImage = false;
			that.btnText = '拍照';
		}
	}
};
</script>

<style lang="less">
@import url('checkin.less');
</style>
