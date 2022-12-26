package com.example.searchfa

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.example.searchfa.databinding.ActivityMainBinding
import com.example.searchfa.fragments.AboutAppFragment
import com.example.searchfa.fragments.BookingsFragment
import com.example.searchfa.fragments.MainFragment
import com.example.searchfa.fragments.SearchFragment
import com.example.searchfa.sqlite.UserDatabase
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardViewSecond.visibility = View.GONE

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeHolder_1, MainFragment.newInstance())
            .commit()
        binding.bottomNavigation.id = R.id.item_search_audience

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_search_audience -> {
                    binding.cardViewSecond.visibility = View.GONE

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.placeHolder_1, MainFragment.newInstance())
                        .commit()

                    val tag = "SearchFragment"
                    supportFragmentManager.findFragmentByTag(tag)?.let { it1 ->
                        supportFragmentManager
                            .beginTransaction()
                            .remove(it1)
                            .commit()
                    }
                }
                R.id.item_about_app -> {
                    binding.cardViewSecond.visibility = View.GONE

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.placeHolder_1, AboutAppFragment.newInstance())
                        .commit()

                    val tag = "SearchFragment"
                    supportFragmentManager.findFragmentByTag(tag)?.let { it1 ->
                        supportFragmentManager
                            .beginTransaction()
                            .remove(it1)
                            .commit()
                    }
                }
                R.id.item_my_booking -> {
                    binding.cardViewSecond.visibility = View.GONE

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.placeHolder_1, BookingsFragment.newInstance())
                        .commit()

                    val tag = "SearchFragment"
                    supportFragmentManager.findFragmentByTag(tag)?.let { it1 ->
                        supportFragmentManager
                            .beginTransaction()
                            .remove(it1)
                            .commit()
                    }
                }
                R.id.item_exit -> {

                    Thread{
                        val sqliteDb = UserDatabase.getDatabase(this)
                        sqliteDb.getDao().deleteUser()
                    }.start()

                    val intent = Intent(this, AuthorizationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
            true
        }
    }

    fun goToSearchRecyclerView() {
        binding.cardViewSecond.visibility = View.VISIBLE

        Thread{
            Thread.sleep(50L)
            binding.scView.scrollTo(binding.cardViewFirst.bottom, binding.cardViewFirst.bottom)
        }.start()
    }

    fun updateSearchFragment() {
        Toast.makeText(this, "Аудитория забронирована", Toast.LENGTH_SHORT).show()

        val tag = "SearchFragment"
        supportFragmentManager.findFragmentByTag(tag)?.let { it1 ->
            supportFragmentManager
                .beginTransaction()
                .remove(it1)
                .commit()
        }

        binding.cardViewSecond.visibility = View.GONE
    }

    fun updateBookingsFragment() {
        Toast.makeText(this, "Бронь отменена", Toast.LENGTH_SHORT).show()

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeHolder_1, BookingsFragment.newInstance())
            .commit()
    }
}