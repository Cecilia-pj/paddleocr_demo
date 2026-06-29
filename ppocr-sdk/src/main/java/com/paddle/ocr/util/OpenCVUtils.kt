// Copyright (c) 2026 PaddlePaddle Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.paddle.ocr.util

import android.content.Context
import android.util.Log
import org.opencv.android.OpenCVLoader

object OpenCVUtils {
    private var initialized = false
    var lastError: String? = null
        private set

    fun init(context: Context): Boolean {
        if (initialized) return true
        lastError = null
        val errors = mutableListOf<String>()

        // 1. 尝试通过 OpenCVLoader 初始化
        try {
            initialized = OpenCVLoader.initDebug()
            if (initialized) {
                Log.i("OpenCVUtils", "OpenCV initialized successfully via OpenCVLoader")
                return true
            }
            val msg = "OpenCVLoader.initDebug() returned false"
            errors.add(msg)
            Log.w("OpenCVUtils", msg)
        } catch (e: Throwable) {
            val msg = "OpenCVLoader failed: ${e.javaClass.simpleName}: ${e.message}"
            errors.add(msg)
            Log.w("OpenCVUtils", msg)
        }

        // 2. 手动按顺序加载 c++_shared 和 opencv_java4
        try {
            System.loadLibrary("c++_shared")
            Log.i("OpenCVUtils", "libc++_shared loaded successfully")
            System.loadLibrary("opencv_java4")
            Log.i("OpenCVUtils", "libopencv_java4 loaded successfully")
            initialized = true
            return true
        } catch (e: Throwable) {
            val msg = "Manual load failed: ${e.javaClass.simpleName}: ${e.message}"
            errors.add(msg)
            Log.e("OpenCVUtils", msg)
        }

        // 3. 直接加载 opencv_java4（不依赖 c++_shared）
        try {
            System.loadLibrary("opencv_java4")
            Log.i("OpenCVUtils", "libopencv_java4 loaded directly (without c++_shared)")
            initialized = true
            return true
        } catch (e: Throwable) {
            val msg = "Direct load failed: ${e.javaClass.simpleName}: ${e.message}"
            errors.add(msg)
            Log.e("OpenCVUtils", msg)
        }

        // 所有尝试均失败，汇总错误信息
        lastError = errors.joinToString(" | ")
        return false
    }
}