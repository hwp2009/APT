package com.www.annotation_complier;

/**
 * @author yulai
 * @time:
 */


import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.www.annotation.Provider;
import com.www.annotation.RouteAnnotation;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

//这个一定要写
@AutoService(Processor.class)
public class AnnotationProcess extends AbstractProcessor {

    private Filer mFiler; //这个filer类主要是为了生成java文件

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
//        return super.getSupportedSourceVersion();
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
//        return super.getSupportedAnnotationTypes();

        LinkedHashSet<String> types = new LinkedHashSet<>();
        types.add(RouteAnnotation.class.getCanonicalName());

        return types; //这个注解处理器只处理批定的注解（也就是我们自己定义的注解） 通过types.add（）方法添加
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        HashMap<String, String> nameMap = new HashMap<>();

//        拿到所有RouteAnnotation注解标注的类
        Set<? extends Element> annotationElements = roundEnv.getElementsAnnotatedWith(RouteAnnotation.class);

        for (Element element : annotationElements) {

            RouteAnnotation annotation = element.getAnnotation(RouteAnnotation.class);

            String name = annotation.name();
            nameMap.put(name, element.getSimpleName().toString()); //这个集合中存了注解的name名字同时把当前页面的 类名也拿到了

        }

        //生成java文件
        generateJavaFile(nameMap);

        return true;
    }

    private void generateJavaFile(HashMap<String, String> nameMap) {

        System.out.println("执行了");

        //这是声明了一个修饰符为public的 并且有一个局部变量 routeMap 的HashMap  一个构造方法
        MethodSpec.Builder constructorBuild = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("routeMap = new $T<>()", HashMap.class);


        for (String key : nameMap.keySet()) {
            String name = nameMap.get(key);
            constructorBuild.addStatement("routeMap.put(\"$N\",\"$N\")", key, name); //在 构造方法中会生成 routemap.put的动作
        }

        MethodSpec constrouctName = constructorBuild.build();

        //一个普通方法
        MethodSpec reouteName = MethodSpec.methodBuilder("getActivityName")
                .addModifiers(Modifier.PUBLIC)
                .returns(String.class)
                .addParameter(String.class, "routeName")
                .beginControlFlow("if(null==routeMap&&!routeMap.isEmpty())")
                .addStatement("return (String) routeMap.get(routeName)")
                .endControlFlow()
                .addStatement("return \"\"")
                .build();

        //generate class生成一个类
        TypeSpec typeSpec = TypeSpec.classBuilder("AnnotationRoute$Finder")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(constrouctName)
                .addMethod(reouteName)
                .addSuperinterface(Provider.class)
                .addField(HashMap.class, "routeMap", Modifier.PRIVATE)
                .build();

        JavaFile javaFile = JavaFile.builder("com.route.page", typeSpec).build();
        try {
            javaFile.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
