package ru.subnak.sapr.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.subnak.sapr.R
import ru.subnak.sapr.presentation.fragment.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit()

        }
    }
}