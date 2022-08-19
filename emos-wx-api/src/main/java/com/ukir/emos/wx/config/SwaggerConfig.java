package com.ukir.emos.wx.config;

import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * Swagger的配置类
 **/
@Configuration //声明为配置类
@EnableOpenApi //声明开启knife4j
public class SwaggerConfig {
    @Bean
    public Docket docket() {
        Docket docket =  new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo()).enable(true)
                .select()
                //apis：扫描包路径，提取API接口,只有使用了@ApiOperation的方法才可以添加到文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                //path：扫描接口路径下的任何方法
                .paths(PathSelectors.any())
                .build();

        //使Swagger开启JWT验证
        /*
         参数：
            name:参数名
            keyname:键名 描述信息
            passAS：接收令牌的位置
         */
        ApiKey apiKey = new ApiKey("token","token","header");

        //将apiKey封装到List里
        List<SecurityScheme> apiKeyList = new ArrayList<>();
        apiKeyList.add(apiKey);
        //点击securitySchemes查看源码，这里需要传入的是SecurityScheme泛型的List，所以上面的List的泛型要为SecurityScheme
        docket.securitySchemes(apiKeyList);
        /*
            设定令牌作用域
            导入这个包：springfox.documentation.service
            参数：
                scope：范围 global是全局
                description：描述
         */
        AuthorizationScope scope = new AuthorizationScope("global","accessEverything");
        AuthorizationScope[] scopes = {scope};
        SecurityReference reference = new SecurityReference("token",scopes);
        //再次封装
        List refList = new ArrayList();
        refList.add(reference);
        SecurityContext context = SecurityContext.builder().securityReferences(refList).build();
        List ctxList = new ArrayList();
        ctxList.add(context);
        docket.securityContexts(ctxList);
        return docket;
    }

    //文档的基本信息
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                //文档标题
                .title("EMOS在线办公系统")
                //文档描述
                .description("用于测试后端返回接口")
                .contact(new Contact("ukir","http://localhost:8080","2416053677"))
                .version("v1.0")
                .build();
    }
}
