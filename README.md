# CoroutinePermissions
用kotlin协程实现动态权限请求

自从6.0加入动态权限之后，很多地方都要用到，开始的时候使用的原生代码写权限请求，代码格式如：
```
if(应用是否已经请求了该权限组){
    权限请求后的代码
}else{
    请求权限
}
```
然后不知道在fragment或是activity里面重写
```
override onRequestPermissionsResult{
    if(请求成功){
        权限请求后的代码
    }else{
        失败处理
    }
｝
```

不知道你们对这种代码是什么感觉，反正我是很不喜欢这样的代码，和不喜欢startActivityForResult还有handle一样，由于今年来rxjava大火，我首先想到的是能不能用rxjava来封装，果然，git上一搜，就搜到[RxPermissions](https://github.com/tbruyelle/RxPermissions)和[RxStartActivityForResult](https://github.com/1670295969/RxStartActivityForResult)这两个项目，这种写法优化了原生代码那种离散的写法，增加了代码的可读性，代码变为
```
new RxPermissions(activity)
    .request(权限数组)
    .subscribe(){
        if(权限请求成功){
        {
    }
```

瞬间简洁了不少，不过考虑到我的很多项目都接入了协程，也觉得协程的顺序写法可读性简洁性和可维护性都更强，希望能够找到请求权限的协程解决方案，在网上找了很久也没找到相关方案，找startActivityForResult的时候却找到了startActivityForResult的协程解决方案[InlineActivityResult](https://github.com/florent37/InlineActivityResult)，于是参考了他的设计思想，最终实现了动态权限请求的协程写法，[用kotlin协程实现动态权限请求](https://github.com/diao-jian/CoroutinePermissions)

用法很简单：
```
CoroutineScope(Dispatchers.Main).launch {
    try {
        requestPermissionForResult(*permissions)//权限请求
        权限请求成功后执行的代码
    }catch (e: InlineRequestPermissionException) {
        权限请求失败后执行的代码
    }
}
```

因为语法要求，所以在执行权限请求前要先开启一个协程CoroutineScope(Dispatchers.Main).launch，因为是在Activity或是fragment中请求，所以最好在协程中指定主线程Dispatchers.Main

trycatch是因为要处理权限取消或是权限请求失败后的流程，也可以在设计的时候集中处理权限请求失败后的流程，这样就可以不用写trycatch,这就涉及到协程的异常处理了

```
CoroutineScope(Dispatchers.Main).launch {
    requestPermissionForResult(*permissions)//权限请求
    权限请求成功后执行的代码
}
```

另外trycatch也可以和其他协程共用，这样trycatch就不是专门为某个协程而生了

```
CoroutineScope(Dispatchers.Main).launch {
    try {
        requestPermissionForResult(*permissions)//权限请求协程写法
        权限请求成功后执行的代码
        val result=startForResult(activity)//startActivityForResult协程写法
        startActivityForResult返回代码
    }catch (e: InlineRequestPermissionException) {
        权限请求失败后执行的代码
    }catch(e: InlineRequestPermissionException){
        startActivityForResult返回异常处理
    }
}
```
实现原理也不复杂，思路是请求权限的时候在Activity中创建一个无界面fragment,提交fragment事务的时候不要提交到回退栈（commitNowAllowingStateLoss），在该fragment中重写onRequestPermissionsResult方法接收请求结果，完毕之后移除fragment事务,然后用协程封装方法封装；这样做的好处是整个权限请求过程和原请求的Activity/Fragment解耦，无需关心请求过程，也无需重写onRequestPermissionsResult去接收请求

无界面fragment提交事务方式：
```
activity.supportFragmentManager
        .beginTransction()
        .add(fragment,Tag)
        .commitNowAllowingStateLoss()
```

无界面fragment移除
```
fragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
```

最后用协程封装
![image.png](https://upload-images.jianshu.io/upload_images/3018427-3d1d3177fccdfb12.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

当然原理还是比较简单，实现过程也不算复杂，这是项目地址：[用kotlin协程实现动态权限请求](https://github.com/diao-jian/CoroutinePermissions)







