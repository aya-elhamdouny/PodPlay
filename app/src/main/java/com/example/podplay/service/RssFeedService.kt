package com.example.podplay.service

import com.example.podplay.BuildConfig
import com.example.podplay.model.RssFeedResponse
import com.example.podplay.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.w3c.dom.Node
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory

class RssFeedService private constructor(){
  suspend fun getFeed(xmlfileUrl :String ) : RssFeedResponse?{

      val service: FeedService
      val interceptor = HttpLoggingInterceptor()
      interceptor.level = HttpLoggingInterceptor.Level.BODY

      val client = OkHttpClient().newBuilder()
              .addInterceptor(interceptor)
              .connectTimeout(30, TimeUnit.SECONDS)
              .writeTimeout(30, TimeUnit.SECONDS)
              .readTimeout(30, TimeUnit.SECONDS)
      if (BuildConfig.DEBUG) {
          client.addInterceptor(interceptor)
      }
      client.build()

      val retrofit = Retrofit.Builder()
              .baseUrl("${xmlfileUrl.split("?")[0]}/")
              .build()
      service = retrofit.create(FeedService::class.java)

      try {
          val result = service.getFeed(xmlfileUrl)
          if (result.code() >= 400) {
              println("server error, ${result.code()}, ${result.errorBody()}")
              return null
          } else {
              var rssFeedResponse : RssFeedResponse? = null
              // return success result
              val dbFactory = DocumentBuilderFactory.newInstance()
              val dBuilder = dbFactory.newDocumentBuilder()
              withContext(Dispatchers.IO) {
                  val doc = dBuilder.parse(result.body()?.byteStream())
                  val rss = RssFeedResponse(episodes = mutableListOf())
                  domToRssFeedResponse(doc, rss)
                  println(rss)
                  rssFeedResponse = rss
              }
              return rssFeedResponse
          }
      } catch (t: Throwable) {
          println("error, ${t.localizedMessage}")
      }
      return null

  }


    companion object{
        val instance : RssFeedService by lazy {
            RssFeedService()
        }
    }


    interface FeedService{
        @Headers(
                "Content-Type: application/xml; charset=utf-8",
                "Accept: application/xml"
        )@GET
        suspend fun getFeed(@Url xmlfileUrl :String) : Response<ResponseBody>

    }


    private fun domToRssFeedResponse(node: Node, rssFeedResponse:
    RssFeedResponse) {
        if (node.nodeType == Node.ELEMENT_NODE) {

            val nodeName = node.nodeName
            val parentName = node.parentNode.nodeName
            val grandParentName = node.parentNode.parentNode?.nodeName ?: ""
            if (parentName == "item" && grandParentName == "channel") {
                val currentItem = rssFeedResponse.episodes?.last()
                if (currentItem != null) {
                    when (nodeName) {
                        "title" -> currentItem.title = node.textContent
                        "description" -> currentItem.description =
                            node.textContent
                        "itunes:duration" -> currentItem.duration =
                            node.textContent
                        "guid" -> currentItem.guid = node.textContent
                        "pubDate" -> currentItem.pubDate = node.textContent
                        "link" -> currentItem.link = node.textContent
                        "enclosure" -> {
                            currentItem.url = node.attributes.getNamedItem("url")
                                .textContent
                            currentItem.type = node.attributes.getNamedItem("type")
                                .textContent
                        }
                    }
                }
            }
            if (parentName == "channel") {
                when (nodeName) {
                    "title" -> rssFeedResponse.title = node.textContent
                    "description" -> rssFeedResponse.description =
                        node.textContent
                    "itunes:summary" -> rssFeedResponse.summary =
                        node.textContent
                    "item" -> rssFeedResponse.episodes?.
                    add(RssFeedResponse.EpisodeResponse())
                    "pubDate" -> rssFeedResponse.lastUpdated =
                        DateUtils.xmlDateToDate(node.textContent)
                }
            }
        }
        val nodeList = node.childNodes
        for (i in 0 until nodeList.length) {
            val childNode = nodeList.item(i)
            domToRssFeedResponse(childNode, rssFeedResponse)
        }
    }



}