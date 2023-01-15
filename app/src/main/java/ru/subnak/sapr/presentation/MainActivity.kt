package ru.subnak.sapr.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.subnak.sapr.R
import ru.subnak.sapr.presentation.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        fun isDarkTheme(activity: Activity): Boolean {
//            return activity.resources.configuration.uiMode and
//                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
//        }
//        if (isDarkTheme(this)) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit()

        }


    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        if (id == R.id.help_button) {
//            Toast.makeText(this,"kek", Toast.LENGTH_SHORT).show()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
}