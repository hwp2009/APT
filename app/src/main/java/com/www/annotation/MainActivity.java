package com.www.annotation;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.route.page.AnnotationRoute$Finder;


/**
 * @author yulai
 * @time:
 */
@RouteAnnotation(name = "Route_MainActivity")
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new AnnotationRoute$Finder().getActivityName(""); //测试这个类生成了
    }
}
