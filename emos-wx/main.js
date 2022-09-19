import App from './App'

// #ifndef VUE3
import Vue from 'vue'
Vue.config.productionTip = false
App.mpType = 'app'
const app = new Vue({
	...App
})
app.$mount()
// #endif



//后端项目地址
let baseUrl = "http://192.168.124.12:8080/emos-wx-api"


/* 全局接口地址 */
Vue.prototype.url = {
	register: baseUrl + "/user/register", //注册用户接口地址
	login: baseUrl + "/user/login", //用户登录接口地址
	checkin: baseUrl + "/checkin/checkin", //签到
	//创建人脸模型
	createFaceModel: baseUrl + "/checkin/createFaceModel", 
	//查看当前时间能否签到
	validCanCheckIn: baseUrl + "/checkin/validCanCheckIn", 
	//获取签到成功的详情
	searchTodayCheckin: baseUrl + "/checkin/searchTodayCheckin", 
	//获取用户概要信息
	searchUserSummary: baseUrl + "/user/searchUserSummary",
	//获取当月考勤数据
	searchMonthCheckin: baseUrl + "/checkin/searchMonthCheckin"
	
	
}

/* 全局权限验证函数 */
Vue.prototype.checkPermission = function(perms){
	//从storage变量里取出用户权限列表
	let permission = uni.getStorageSync("permission");
	let result = false;
	for(let one of perms){
		// 从数组中取出变量和permission中的值比较
		if(permission.indexOf(one) != -1){
			result = true;
			break;
		}
	}
	return result;
}






/*
	全局封装Ajax
	参数：
	url:请求地址
	method:请求方式
	data:请求数据
	fun:匿名函数
*/
Vue.prototype.ajax = function(url, method, data, fun) {
	uni.request({
		"url": url,
		"method": method,
		"header": {
			token: uni.getStorageSync("token"), //从Storage中获取保存的token
		},
		"data": data,
		success: function(resp) { //成功的回调
			//如果返回的状态码为401,则表示用户未登录，跳转登录页面
			if (resp.statusCode == 401) {
				uni.redirectTo({
					url: "/pages/login/login.vue"
				})

			} else if (resp.statusCode == 200 && resp.data.code == 200) { //如果响应状态码和业务状态码都为200

				let data = resp.data;
				//判断返回的数据中是否有token
				if (data.hasOwnProperty("token")) {
					let token = data.token;
					console.log("token " + token);
					//将token存储到Storage中
					uni.setStorageSync("token", token);
				}
				//匿名函数，自己定义
				fun(resp)
			}
			//异常提示
			else {
				uni.showToast({
					icon: "none",
					title: resp.data,
				})

			}
		},
	})
}

// #ifdef VUE3
import {
	createSSRApp
} from 'vue'
export function createApp() {
	const app = createSSRApp(App)
	return {
		app
	}
}
// #endif
