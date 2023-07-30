package com.apro.volvo.ui

import android.R.attr
import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.apro.volvo.ui.theme.VolvoTheme
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date

val ARG_ACCOUNT_TYPE = "accountType"
val ARG_AUTH_TOKEN_TYPE = "authTokenType"
val ARG_IS_ADDING_NEW_ACCOUNT = "isAddingNewAccount"
val PARAM_USER_PASSWORD = "password"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {



  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)



    setContent {
      VolvoTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          Greeting("Android")
        }
      }
    }
  }


}



private class OnTokenAcquired : AccountManagerCallback<Bundle> {

  override fun run(result: AccountManagerFuture<Bundle>) {
    // Get the result of the operation from the AccountManagerFuture.
    val bundle: Bundle = result.result

    // The token is a named value in the bundle. The name of the value
    // is stored in the constant AccountManager.KEY_AUTHTOKEN.
    val token: String? = bundle.getString(AccountManager.KEY_AUTHTOKEN)

    println(">>> tokeN " + token)
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(
    text = "Hello $name!",
    modifier = modifier
  )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
  VolvoTheme {
    Greeting("Android")
  }
}

public class MyAuthenticator(val context: Context) : AbstractAccountAuthenticator(context) {
  override fun editProperties(response: AccountAuthenticatorResponse?, accountType: String?): Bundle? {
    return null
  }

  override fun addAccount(
    response: AccountAuthenticatorResponse?,
    accountType: String?,
    authTokenType: String?,
    requiredFeatures: Array<out String>?,
    options: Bundle?,
  ): Bundle {
    val reply = Bundle()

    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
    intent.putExtra(ARG_ACCOUNT_TYPE, attr.accountType)
    intent.putExtra(ARG_AUTH_TOKEN_TYPE, authTokenType)
    intent.putExtra(ARG_IS_ADDING_NEW_ACCOUNT, true)
    reply.putParcelable(AccountManager.KEY_INTENT, intent)
    return reply
  }

  override fun confirmCredentials(response: AccountAuthenticatorResponse?, account: Account?, options: Bundle?): Bundle? {
    return null
  }

  override fun getAuthToken(response: AccountAuthenticatorResponse?, account: Account, authTokenType: String?, options: Bundle?): Bundle {
    val am = AccountManager.get(context)

    var authToken = am.peekAuthToken(account, authTokenType)

    // Lets give another try to authenticate the user

    // Lets give another try to authenticate the user
    if (null != authToken) {
      if (authToken.isEmpty()) {
        val password = am.getPassword(account)
        if (password != null) {
          authToken = AccountUtils.mServerAuthenticator.signIn(account.name, password)
        }
      }
    }

    // If we get an authToken - we return it

    // If we get an authToken - we return it
    if (null != authToken) {
      if (!authToken.isEmpty()) {
        val result = Bundle()
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
        result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
        return result
      }
    }

    // If we get here, then we couldn't access the user's password - so we
    // need to re-prompt them for their credentials. We do that by creating
    // an intent to display our AuthenticatorActivity.

    // If we get here, then we couldn't access the user's password - so we
    // need to re-prompt them for their credentials. We do that by creating
    // an intent to display our AuthenticatorActivity.
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
    intent.putExtra(ARG_ACCOUNT_TYPE, account.type)
    intent.putExtra(ARG_AUTH_TOKEN_TYPE, authTokenType)

    // This is for the case multiple accounts are stored on the device
    // and the AccountPicker dialog chooses an account without auth token.
    // We can pass out the account name chosen to the user of write it
    // again in the Login activity intent returned.

    // This is for the case multiple accounts are stored on the device
    // and the AccountPicker dialog chooses an account without auth token.
    // We can pass out the account name chosen to the user of write it
    // again in the Login activity intent returned.

      intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name)


    val bundle = Bundle()
    bundle.putParcelable(AccountManager.KEY_INTENT, intent)

    return bundle
  }

  override fun getAuthTokenLabel(authTokenType: String?): String? {
    return null
  }

  override fun updateCredentials(
    response: AccountAuthenticatorResponse?,
    account: Account?,
    authTokenType: String?,
    options: Bundle?,
  ): Bundle? {
    return null
  }

  override fun hasFeatures(response: AccountAuthenticatorResponse?, account: Account?, features: Array<out String>?): Bundle? {
    return null
  }
}

object AccountUtils {
  const val ACCOUNT_TYPE = "com.samugg.example"
  const val AUTH_TOKEN_TYPE = "com.samugg.example.aaa"
  var mServerAuthenticator: IServerAuthenticator = MyServerAuthenticator()
  fun getAccount(context: Context?, accountName: String?): Account? {
    val accountManager = AccountManager.get(context)
    val accounts = accountManager.getAccountsByType(ACCOUNT_TYPE)
    for (account in accounts) {
      if (account.name.equals(accountName, ignoreCase = true)) {
        return account
      }
    }
    return null
  }
}

interface IServerAuthenticator {
  /**
   * Tells the server to create the new user and return its auth token.
   * @param email
   * @param username
   * @param password
   * @return Access token
   */
  fun signUp(email: String?, username: String?, password: String?): String?

  /**
   * Logs the user in and returns its auth token.
   * @param email
   * @param password
   * @return Access token
   */
  fun signIn(email: String?, password: String?): String?
}

class MyServerAuthenticator : IServerAuthenticator {
  override fun signUp(email: String?, username: String?, password: String?): String? {
    // TODO: register new user on the server and return its auth token
    return null
  }

  override fun signIn(email: String?, password: String?): String? {
    var authToken: String? = null
    val df: DateFormat = SimpleDateFormat("yyyyMMdd-HHmmss")
    if (mCredentialsRepo!!.containsKey(email)) {
      if (password == mCredentialsRepo!![email]) {
        authToken = email + "-" + df.format(Date())
      }
    }
    return authToken
  }

  companion object {
    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private var mCredentialsRepo: Map<String?, String>? = null

    init {
      val credentials: MutableMap<String?, String> = HashMap()
      credentials["demo@example.com"] = "demo"
      credentials["foo@example.com"] = "foobar"
      credentials["user@example.com"] = "pass"
      mCredentialsRepo = Collections.unmodifiableMap(credentials)
    }
  }
}
