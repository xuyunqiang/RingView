## 在布局文件中的样子

---

```
    <!-- 积分环形控件 -->
    <RelativeLayout
        android:id="@+id/ring_RL"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center">

        <com.github.customview.views.RingView
            android:id="@+id/level_ring_view"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerHorizontal="true"
            level_ring:inner_ring_width="12dp"
            level_ring:level_text_size="12sp"
            level_ring:wide_ring_width="20dp"
            />
        
    </RelativeLayout>
```

## 在代码中的样子

---

```
    ringView.setOnFinishListener(new RingView.OnFinishListener() {
        @Override
        public void onFinish() {
            Toast.makeText(getActivity(), "绘制完成", Toast.LENGTH_SHORT).show();
        }
    });

    ringView.setLevel(30);
```

## 控件效果图
![ringView](http://7xq7wz.com1.z0.glb.clouddn.com/test.gif)

