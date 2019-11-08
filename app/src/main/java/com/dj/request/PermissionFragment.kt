package com.dj.request

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dj.coroutines.permisstions.InlineRequestPermissionException
import com.dj.coroutines.permisstions.requestPermissionsForResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PermissionFragment:Fragment(){
    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_permissions,container,false)
        initView()
        return view
    }
    private fun initView(){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                requestPermissionsForResult(*permissions)
                Toast.makeText(activity,"权限请求成功", Toast.LENGTH_SHORT).show()
            }catch (e: InlineRequestPermissionException){
                Toast.makeText(activity,"权限请求失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}