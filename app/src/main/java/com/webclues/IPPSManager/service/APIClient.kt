package com.webclues.IPPSManager.service



import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.webclues.IPPSManager.utility.Content
import com.webclues.IPPSManager.utility.TinyDb
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class APIClient {


    companion object {


        fun getretrofit(context: Context): Retrofit {

            //eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjM4ZmJmMDI0OWU2YTMyMDJiMzQxMzkxZDhmMjhjMjE4MzgyNWM0YTE2Y2M5YjcwZDk1OGEyZGNiODgxNzVjZWMxODAyZjY1OWU4MjlkMWY0In0.eyJhdWQiOiI1NyIsImp0aSI6IjM4ZmJmMDI0OWU2YTMyMDJiMzQxMzkxZDhmMjhjMjE4MzgyNWM0YTE2Y2M5YjcwZDk1OGEyZGNiODgxNzVjZWMxODAyZjY1OWU4MjlkMWY0IiwiaWF0IjoxNTg0MDc5Mzk0LCJuYmYiOjE1ODQwNzkzOTQsImV4cCI6MTYxNTYxNTM5NCwic3ViIjoiMzQiLCJzY29wZXMiOltdfQ.vAglSSZRyPMz7Wbzrl-1w77ObxU7sxlGrG-XGalXmqMU32XFGjcSe8VpZGDrHOXMDKGqS1Zo9x5HmtqZn-njplJ_jWp7cHVJeoInfgYrAVOhoBDQ1BT4qImC4tMoYm-IKWRGDbf0Byd6dtS3A2x3czBqR0LxfkbbMHJXrwPTYE91SLctJlTxiB-5LMoA2XUf3IiRsYI9ntDXUNX7ajuhK5fmW2WO7lfLwKZlQ64kFlhPbQKgVhyY4H_DU1367ZrX94APL9bIDElG6CBwYjUgUj5-7rL0AcFQdadvo-jNn-nYmtfahlEMf6pBM3JBFujlTxxN4ETHwIF431pRfrJByYU7y7Ftb-6iQowApfKHcNAk01OlpmED-3Di07vg1LvyVRlqMGlgcqNUxCxAZFSw59V8om1EM4eFGNvip0rZdRjQoslpBmPE9SpUrqvxzMgae4H1I_2JY3yU5xKPgkweP0cN_eKojORFgY3R6yFwrDNRT-XBPsSwo9o_eDly3O9v2lkqDsWn_yNQzc1V8-sMvr5YBKKRLpb59AxRD2gSKsAbsS-00sZYZ54ydPc71iEJQ6hw-AgbsWeTTh_vXlTUcr06ryvnzQVoNacU0s5tDkQKtDXN0n3QjAr2vLcTn01vTz2nbc0sqCiY43lzXi1LPdN9asBOSmX03bHmsljvzWk


            //eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjM4ZmJmMDI0OWU2YTMyMDJiMzQxMzkxZDhmMjhjMjE4MzgyNWM0YTE2Y2M5YjcwZDk1OGEyZGNiODgxNzVjZWMxODAyZjY1OWU4MjlkMWY0In0.eyJhdWQiOiI1NyIsImp0aSI6IjM4ZmJmMDI0OWU2YTMyMDJiMzQxMzkxZDhmMjhjMjE4MzgyNWM0YTE2Y2M5YjcwZDk1OGEyZGNiODgxNzVjZWMxODAyZjY1OWU4MjlkMWY0IiwiaWF0IjoxNTg0MDc5Mzk0LCJuYmYiOjE1ODQwNzkzOTQsImV4cCI6MTYxNTYxNTM5NCwic3ViIjoiMzQiLCJzY29wZXMiOltdfQ.vAglSSZRyPMz7Wbzrl-1w77ObxU7sxlGrG-XGalXmqMU32XFGjcSe8VpZGDrHOXMDKGqS1Zo9x5HmtqZn-njplJ_jWp7cHVJeoInfgYrAVOhoBDQ1BT4qImC4tMoYm-IKWRGDbf0Byd6dtS3A2x3czBqR0LxfkbbMHJXrwPTYE91SLctJlTxiB-5LMoA2XUf3IiRsYI9ntDXUNX7ajuhK5fmW2WO7lfLwKZlQ64kFlhPbQKgVhyY4H_DU1367ZrX94APL9bIDElG6CBwYjUgUj5-7rL0AcFQdadvo-jNn-nYmtfahlEMf6pBM3JBFujlTxxN4ETHwIF431pRfrJByYU7y7Ftb-6iQowApfKHcNAk01OlpmED-3Di07vg1LvyVRlqMGlgcqNUxCxAZFSw59V8om1EM4eFGNvip0rZdRjQoslpBmPE9SpUrqvxzMgae4H1I_2JY3yU5xKPgkweP0cN_eKojORFgY3R6yFwrDNRT-XBPsSwo9o_eDly3O9v2lkqDsWn_yNQzc1V8-sMvr5YBKKRLpb59AxRD2gSKsAbsS-00sZYZ54ydPc71iEJQ6hw-AgbsWeTTh_vXlTUcr06ryvnzQVoNacU0s5tDkQKtDXN0n3QjAr2vLcTn01vTz2nbc0sqCiY43lzXi1LPdN9asBOSmX03bHmsljvzWk

         /*   if (retofit == null) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                val okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(object : Interceptor {
                        override fun intercept(chain: Interceptor.Chain): Response {

                            var request = chain.request();
                            var requestBuilder: Request.Builder = request.newBuilder()
                                .addHeader(
                                    "Authorization", "Bearer " +
                                            TinyDb(context).getString(Content.AUTORIZATION_TOKEN)
                                );
                            request = requestBuilder.build();
                            return chain.proceed(request);


                        }

                    })
                    .readTimeout(600, TimeUnit.SECONDS)
                    .connectTimeout(600, TimeUnit.SECONDS)
                    .build()

                *//*   val gsonBuilder = GsonBuilder()
                   gsonBuilder.setLenient()
                   val gson = gsonBuilder.create()*//*
                retofit = Retrofit.Builder()
                    .baseUrl(Content.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            }
            return retofit!!*/

            val okClient = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .addInterceptor { chain ->
                    val originalRequest = chain.request()
                    val requestBuilder = originalRequest.newBuilder()
                        .method(originalRequest.method, originalRequest.body)
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .addNetworkInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .addHeader("Authorization", TinyDb(context).getString(Content.AUTORIZATION_TOKEN))
                        .addHeader("Accept", "application/json")
                    chain.proceed(requestBuilder.build())
                }
                .callTimeout(200, TimeUnit.MINUTES)
                .connectTimeout(200, TimeUnit.SECONDS)
                .readTimeout(200, TimeUnit.SECONDS)
                .writeTimeout(200, TimeUnit.SECONDS)
                .build();

            val retofit = Retrofit.Builder()
                .baseUrl(Content.BASE_URL)
                .client(okClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            /* val rssService: ApiInterface = retofit!!.create(ApiInterface::class.java)
             return rssService!!*/
            return retofit

        }

    }


}