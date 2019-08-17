package com.dazhukeji.douwu.ui.aty.mine;

import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dazhukeji.douwu.R;
import com.dazhukeji.douwu.adapter.ChatInfoAdpter;
import com.dazhukeji.douwu.api.Config;
import com.dazhukeji.douwu.base.BaseAty;
import com.dazhukeji.douwu.manager.RecyclerViewManager;
import com.zhangyunfei.mylibrary.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.api.BasicCallback;

/**
 * 创建者：zhangyunfei
 * 创建时间：2018/11/28 10:43
 * 功能描述：
 */
public class MemberChatDetailsAty extends BaseAty {

    @BindView(R.id.txt_title)
    TextView txtTitle;
    //    @BindView(R.id.headsRecyclerView)
    //    RecyclerView headsRecyclerView;
    @BindView(R.id.chatInfoRecyclerView)
    RecyclerView chatInfoRecyclerView;
    @BindView(R.id.contentEdit)
    EditText contentEdit;

    private RecyclerViewManager mRecyclerViewManager;
    private List<Message> mList = new ArrayList<>();
    private Conversation mConversation;
    private ChatInfoAdpter mChatInfoAdpter;
    private String mTargetId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_member_chat_details;
    }

    @Override
    public void initView() {
//        txtTitle.setText("How are you doing");
//        mList = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            mList.add(new Object());
//        }
        //        mRecyclerViewManager = new RecyclerViewManager(headsRecyclerView);
        //        mRecyclerViewManager.setLinearLayoutManager(RecyclerView.HORIZONTAL);
        //        headsRecyclerView.setAdapter(new ChatHeadsAdapter(R.layout.heads_item,mList));
        //        headsRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
        //            @Override
        //            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //                super.getItemOffsets(outRect, view, parent, state);
        //                if (parent.getChildAdapterPosition(view)!=state.getItemCount()-1){
        //                    outRect.right= DisplayHelper.dp2px(mContext,17);
        //                }
        //            }
        //        });

        mRecyclerViewManager = new RecyclerViewManager(chatInfoRecyclerView);
        mRecyclerViewManager.setLinearLayoutManager(RecyclerView.VERTICAL);
        mChatInfoAdpter = new ChatInfoAdpter(R.layout.chat_info_item, mList);
        chatInfoRecyclerView.setAdapter(mChatInfoAdpter);

        mTargetId = getIntent().getStringExtra("targetId");
        getData();
    }

    private void getData(){
        mConversation = JMessageClient.getSingleConversation(mTargetId, Config.getAppkey());
        List<Message> allMessage = mConversation.getAllMessage();
        if (null != allMessage && allMessage.size()>0){
            mChatInfoAdpter.setNewData(allMessage);
        }
    }

    @Override
    public void initData() {

    }

    @OnClick(R.id.sendImg)
    public void onViewClicked() {
        String content = contentEdit.getText().toString();
        if (!StringUtils.isEmpty(content)){
            Message message = mConversation.createSendMessage(new TextContent(content));
            message.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int responseCode, String responseDesc) {
                    if (responseCode == 0) {
                        //消息发送成功
                        Toast.makeText(MemberChatDetailsAty.this, "消息发送成功", Toast.LENGTH_SHORT).show();
                        getData();
                    } else {
                        //消息发送失败
                        Toast.makeText(MemberChatDetailsAty.this, "消息发送失败", Toast.LENGTH_SHORT).show();
                    }
                    contentEdit.getText().clear();
                }
            });

            MessageSendingOptions options = new MessageSendingOptions();
            options.setRetainOffline(false);

            JMessageClient.sendMessage(message);//使用默认控制参数发送消息
        }else {
            Toast.makeText(this, "发送内容不能为空", Toast.LENGTH_SHORT).show();
        }

    }
}
