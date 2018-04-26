
## 简介

该项目用于为 View 提供 overScroll 效果和 添加 Header/Footer 布局

* 使用 NestedScrollingParent 接口实现,实现简单,性能很好

## 依赖

```
allprojects {
repositories {
	maven { url 'https://jitpack.io' }
	}
}
```
```
dependencies {
	compile 'com.github.threekilogram:OverScrollLayout:1.1'
}
```

## OverScrollLayout

用于给View增加overScroll效果

* 支持 overScroll
* 自带阻尼效果
* 支持fling到边界布局继续未完成的fling

### 阻尼

![](/img/pic01.gif)

### fling

![](/img/pic02.gif)

## HeaderFooterLayout

用于给View增加header footer,直接包裹住View,设置Header 和 Footer就行,因为该布局扩展自 `OverScrollContainer` 支持所有overScroll的特性

* 自定义Header footer
	* 理论上支持任何view
* 自由的控制显示过程
	* 控制overScroll进度
	* 控制overScroll停止位置
	* 数据变化后重布局header/footer位置
	* overScroll位置微调

### 示例

布局,包裹住 RecyclerView 就行

```
<com.example.overscroll.HeaderFooterLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:id="@+id/headerFooter"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    <android.support.v7.widget.RecyclerView
            android:id="@+id/recycler"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:overScrollMode="never"
            />

</com.example.overscroll.HeaderFooterLayout>
```

设置Header Footer

```
mHeaderFooter = rootView.findViewById(R.id.headerFooter);
mHeaderFooter.setHeader(R.layout.item_main_recycler);
mHeaderFooter.setFooter(R.layout.item_main_recycler);
```

监听

```

// 手指向下拉回调该方法
@Override
public void onScrollOverTop(View header, int scrollY) {
    ((TextView) header).setText(String.valueOf(scrollY));

	// 超过一定距离,手指抬起之后就不回弹到原点
    if (scrollY < -200) {
        mHeaderFooter.stopSpringBack();
    }
}

// 手指抬起之后回调该方法
@Override
public void onOverTopTouchUp(View header, int scrollY) {
    ((TextView) header).setText(" refreshing ");

	// 下拉超过一定距离,回弹到该距离
    if (scrollY < -200) {
        int dy = scrollY - -200;
		//该方法可以微调回弹位置
        mHeaderFooter.scrollBack(-dy);
    }

	//模拟加载数据
    mHeaderFooter.postDelayed(new Runnable() {
        @Override
        public void run() {

			//加载完成后,回弹到原点
            mHeaderFooter.springBack();
        }
    }, 3000);
}
```

![](/img/pic03.gif)

footer 回调

```
@Override
public void onScrollOverBottom(View footer, int scrollY) {

    ((TextView) footer).setText(String.valueOf(scrollY));

	// 当底部overScroll时,直接停止回弹
    mHeaderFooter.stopSpringBack();
}
@Override
public void onOverBottomTouchUp(View footer, int scrollY) {
		
	// 修正一下回弹位置,在上拉距离过大时,修正到一个较小的位置
    if (scrollY > 200) {
        int dy = scrollY - 200;
        mHeaderFooter.scrollBack(-dy);
    }

	// 模拟添加数据
    mHeaderFooter.postDelayed(new Runnable() {
        @Override
        public void run() {
            List< Integer > list = mAdapter.getList();
            final int size = list.size();
            for (int i = 0; i < 10; i++) {
                list.add(size + i);
            }
			//先通知recycler 数据增加了
            mAdapter.notifyDataSetChanged();

			//之后重新安放header和footer位置
            mHeaderFooter.reLayout();
        }
    }, 3000);
}
```

![](/img/pic04.gif)

