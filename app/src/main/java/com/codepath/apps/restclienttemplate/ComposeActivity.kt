package com.codepath.apps.restclienttemplate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var charCount: TextView

    lateinit var client: TwitterClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        client = TwitterApplication.getRestClient(this)
        charCount = findViewById(R.id.charCount)

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                val tweetContent = etCompose.text.toString()
                btnTweet.isEnabled = tweetContent.isNotEmpty() && tweetContent.length <= 280
                charCount.text = tweetContent.length.toString()
            }
        })

        btnTweet.setOnClickListener {
            // grab content of edittext (etcompose)
            val tweetContent = etCompose.text.toString()

            // make sure tweet is not empty
            if (tweetContent.isEmpty()) {
                Toast.makeText(
                    this,
                    "Empty tweets are NOT allowed, big mistake!",
                    Toast.LENGTH_SHORT
                ).show()
            } else
            // also make sure tweet is within character count
                if (tweetContent.length > 280) {
                    Toast.makeText(this, "Tweet is too long, SAD!", Toast.LENGTH_SHORT).show()
                } else {
                    client.publishTweet(tweetContent, object : JsonHttpResponseHandler() {
                        override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                            Log.i(TAG, "Hell yea you did it")

                            val tweet = Tweet.fromJson(json.jsonObject)

                            val intent = Intent()
                            intent.putExtra("tweet", tweet)
                            setResult(RESULT_OK, intent)
                            finish()
                        }
                        override fun onFailure(
                            statusCode: Int,
                            headers: Headers?,
                            response: String?,
                            throwable: Throwable?
                        ) {
                            Log.e(TAG, "Failed to publish tweet", throwable)
                        }

                    })

                }
        }
    }
    companion object {
        val TAG = "ComposeActivity"
    }
}