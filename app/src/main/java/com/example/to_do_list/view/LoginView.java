package com.example.to_do_list.view;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.to_do_list.R;
import com.example.to_do_list.model.User;
import com.example.to_do_list.presenter.MainPresenter;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginView extends AppCompatActivity {

    ImageView image_logo, image_text;
    Button button_signin, button_signup;
    EditText edit_username,edit_password;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_view);
        Bmob.initialize(this,"d5e1932be62f2f2dd6c28344be5fb2f0");
        initRes();
        playAnimtion();
    }

    //初始化布局
    private void initRes() {
        //获得实例
        image_text = findViewById(R.id.image_loginText);
        image_logo = findViewById(R.id.image_loginLogo);
        button_signin = findViewById(R.id.button_signIn);
        button_signup = findViewById(R.id.button_signUp);
        edit_username = findViewById(R.id.edit_username);
        edit_password = findViewById(R.id.edit_password);

        //注册点击事件
        button_signin.setOnClickListener((v) -> {
            signIn();
        });
        button_signup.setOnClickListener((v) -> {
            signUp();
        });
    }

    //页面动画
    private void playAnimtion() {

        //view透明度初始化为不可见
        edit_username.setAlpha(0f);edit_password.setAlpha(0f);image_text.setAlpha(0f);button_signin.getBackground().setAlpha(1);button_signup.getBackground().setAlpha(1);

        //logo的移动以及缩放
        Animator moveAnimator = AnimatorInflater.loadAnimator(this,R.animator.logomove);
        moveAnimator.setTarget(image_logo);
        moveAnimator.start();

        //从无到有
        Animator appear1 = AnimatorInflater.loadAnimator(this, R.animator.appearfloat);
        appear1.setTarget(edit_username);
        Animator appear2 = AnimatorInflater.loadAnimator(this, R.animator.appearfloat);
        appear2.setTarget(edit_password);
        Animator appear3 = AnimatorInflater.loadAnimator(this, R.animator.appearfloat);
        appear3.setTarget(button_signin);
        Animator appear4 = AnimatorInflater.loadAnimator(this, R.animator.appearfloat);
        appear4.setTarget(image_text);
        Animator appear5 = AnimatorInflater.loadAnimator(this, R.animator.appearint);
        appear5.setTarget(button_signin.getBackground());
        Animator appear6 = AnimatorInflater.loadAnimator(this, R.animator.appearint);
        appear6.setTarget(button_signup.getBackground());

        //共同播放
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(appear1).with(appear2).with(appear3).with(appear4).with(appear5).with(appear6);
        animatorSet.start();
    }

    //登陆按钮点击事件
    private void signIn()
    {
        String username = edit_username.getText().toString();
        String password = edit_password.getText().toString();
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", username);
        query.addWhereEqualTo("password", password);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null) {
                    if (list.size() > 0) {
                        user = list.get(0);
                        MainPresenter.setUser(user);
                        show("登录成功");
                        Intent intent = new Intent(LoginView.this,MainView.class);
                        startActivity(intent);
                    } else {
                        show("用户名或密码不正确");
                    }
                } else {
                    show("服务器连接发生错误");
                }
            }
        });
    }

    private void signUp() {
        String username = edit_username.getText().toString();
        String password = edit_password.getText().toString();

        //查询是否已存在该用户名，无则注册
        BmobQuery<User> query = new BmobQuery<User>();
        query.addWhereEqualTo("username", username);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if(e == null) {
                    if (list.size() > 0) {
                        show("此用户名已被占用");
                    } else {
                        user = new User();
                        user.setUsername(username);
                        user.setPassword(password);
                        user.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    show("注册成功");
                                } else {
                                    show("注册失败");
                                }
                            }
                        });
                    }
                } else {
                    show("服务器连接发生错误");
                }
            }
        });
    }

    private void show(String str) {
        Toast.makeText(this,str,Toast.LENGTH_SHORT).show();
    }

}
