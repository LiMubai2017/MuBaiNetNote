package com.example.to_do_list.presenter;

import android.util.Log;

import com.example.to_do_list.model.BeginningCompare;
import com.example.to_do_list.model.DeadlineCompare;
import com.example.to_do_list.model.PriorityCompare;
import com.example.to_do_list.model.Tag;
import com.example.to_do_list.model.User;
import com.example.to_do_list.view.MainView;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by abc on 2017/12/14.
 */

public class MainPresenter {
    public static final int BEGINNING = 1;
    public static final int DEADLINE = 2;
    public static final int PRIORITY = 3;
    private static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        MainPresenter.user = user;
    }

    private MainView view;
    public static List<Tag> tagList;
    public static int rankMode = PRIORITY;

    public MainPresenter(MainView view) {
        this.view = view;
    }

    //初始化
    public void init() {
        initTagList();
    }

    //初始化Tag列表
    public void initTagList() {

        BmobQuery<Tag> query = new BmobQuery<Tag>();
        query.addWhereContainedIn("objectId",user.getObjectIdList());
        query.findObjects(new FindListener<Tag>() {
            @Override
            public void done(List<Tag> list, BmobException e) {
                //获得返回的tag序列
                tagList = new ArrayList<Tag>();
                for(Tag temp:list) {
                    Tag tag = new Tag("");
                    cpy(tag,temp);
                    tagList.add(tag);
                }

                if(e == null) {
                    view.show("更新成功");
                } else {
                    tagList = new ArrayList<Tag>();
                    view.show("更新失败");
                }
                if(tagList.size()==0) {
                    Tag tag = new Tag("欢迎使用木白便签");
                    tagList.add(tag);
                }
                view.initRes();
            }
        });
    }

    //创建新Tag实例
    public void newTag() {
        Tag tag = new Tag("");
        tagList.add(tag);
    }

    //储存TagList到数据库
    public void saveTagsToDataBase() {
        List<BmobObject> tempList = new ArrayList<BmobObject>();

        //批量删除原数据
        for(String objectId:user.getObjectIdList()) {
            Tag temp = new Tag("");
            temp.setObjectId(objectId);
            tempList.add(temp);
        }
        new BmobBatch().deleteBatch(tempList).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> o, BmobException e) {
                if(e==null){
                    for(int i=0;i<o.size();i++){
                        BatchResult result = o.get(i);
                        BmobException ex =result.getError();
                        if(ex==null){
                            Log.i("Bmob","第"+i+"个数据批量删除成功");
                        }else{
                            Log.i("Bmob","第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });

        //批量加入新数据
        tempList.clear();
        for(Tag temp:tagList) {
            tempList.add(temp);
        }
        new BmobBatch().insertBatch(tempList).doBatch(new QueryListListener<BatchResult>() {
            @Override
            public void done(List<BatchResult> o, BmobException e) {
                List idList = new ArrayList();
                if(e==null){
                    for(int i=0;i<o.size();i++){
                        BatchResult result = o.get(i);
                        idList.add(result.getObjectId());
                    }
                    //先储存完tag后回调储存objectId到user表中
                    Log.i("bomb","idlist.size: "+idList.size());

                    User tempUser = new User();
                    //tempUser.setValue("objectIdList",tempList);
                    tempUser.setValue("objectIdList", idList);
                    tempUser.update(user.getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e!=null) {
                                view.show("云储存出现错误,error3");
                            }
                        }
                    });
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }

            }
        });

    }

    public void cpy(Tag target, Tag source) {
        target.setContent(source.getContent());target.setPriority(source.getPriority());target.setNotified(source.isNotified());
        target.set(source.getYear_begin(),source.getYear_end(),source.getMonth_begin(),source.getMonth_end(),source.getDay_begin(),source.getDay_end(),source.getHour_begin(),source.getHour_end(),source.getMinute_begin(),source.getMinute_end());
    }
    
    //Tag排序并刷新View
    public void sort(int rankMode) {
        this.rankMode = rankMode;
        if(rankMode == BEGINNING) Collections.sort(tagList,new BeginningCompare());
        if(rankMode == DEADLINE) Collections.sort(tagList,new DeadlineCompare());
        if(rankMode == PRIORITY) Collections.sort(tagList,new PriorityCompare());
        view.refresh();
    }
}
