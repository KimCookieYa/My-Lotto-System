package com.example.LottoSystem

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.LottoSystem.ui.viewpager_adapter
import com.fxn.ariana.ArianaBackgroundListener
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jsoup.Jsoup
import java.util.*

class MainActivity : AppCompatActivity() {

    private val fragmentManager: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // chip_navigation: activity_main.xml 에서 네비게이션바 id
        // id: menu 디렉토리에서 정한 각 item 의 id
        menu.setOnItemSelectedListener { id ->
            when (id) {
                R.id.mood -> viewpager.currentItem = 0
                R.id.habit_tracker -> viewpager.currentItem = 1
                R.id.add -> viewpager.currentItem = 2
                R.id.chart -> viewpager.currentItem = 3
                R.id.profile -> viewpager.currentItem = 4
            }
        }
        //view_pg: activity_main.xml에서 viewpager의 id

        viewpager.adapter = viewpager_adapter(
            fragmentManager
        ).apply {
            list = ArrayList<String>().apply {
                add("Mood")
                add("Habit")
                add("Add")
                add("Chart")
                add("Profile")
            }
        }

        //img_view: activity_main.xml의 이미지뷰 id
        viewpager.addOnPageChangeListener(
            ArianaBackgroundListener(
                getColors(), img_view, viewpager
            )
        )

        val address = "https://dhlottery.co.kr/gameResult.do?method=byWin"
        var myLotto = arrayOf(0,0,0,0,0,0)
        var str = ""

        doAsync {
            val doc = Jsoup.connect(address).get()
            str = doc.select("meta")[2].attr("content")
            str = str.split(". ")[0]
            winningNumberText.text = str

        }


        randomNumberBtn.setOnClickListener { // 1~45인 랜덤번호 6개 생성
            for (index1 in 0..5) {
                var num: Int = 0

                while (true) {
                    num = rand(1, 46)
                    var index2: Int = 0
                    for (index3 in 0..index1) {
                        index2 = index3
                        if (num == myLotto[index3]) break
                    }
                    if (index1 <= index2) {
                        myLotto[index1] = num
                        break
                    }
                }
            }
            myLotto.sort()

            var stringTemp = ""

            for (index in 0..5) {
                stringTemp += "${myLotto[index]}"
                if (index < 5) stringTemp += ", "
            }

            randomNumberText.text = "Random Number\n$stringTemp"
        }
        resultBtn.setOnClickListener { // 번호 입력하는 새 창으로 넘어감

        }
    }

    private fun rand(from: Int, to: Int) : Int {
        // from와 to 사이의 값을 랜덤으로 생성하는 함수
        val dice = Random()
        return dice.nextInt(to - from) + from
    }
    private fun makeToast(str: String) {
        // 문자열을 받아 토스트를 만들어주는 함수
        Toast.makeText(this@MainActivity, "$str", Toast.LENGTH_SHORT).show()
    }
    private fun answer(str : String) : Array<Int> {
        // 이번주 당첨 로또 값을 배열 형태로 반환하는 함수
        val temp1: String = str.split(". ")[0].split(" ")[3]
        var winningAnswer: Array<Int> = arrayOf(0,0,0,0,0,0,0)
        var temp2: List<String> = temp1.split(",")
        for (index in 0..5) {
            if (index < 5) winningAnswer[index] = Integer.parseInt(temp2[index])
            else {
                winningAnswer[index] = Integer.parseInt(temp2[index].split("+")[0])
                winningAnswer[index+1] = Integer.parseInt(temp2[index].split("+")[1])
                break
            }
        }

        return winningAnswer
    }
    private fun result(answer: Array<Int>, mine: Array<Int>) {
        // 이번주 로또 등수를 확인하는 함수
        var count = 0
        var bonus = 0
        for (index in 0..5) {
            if (answer[index] == mine[index]) count++
            if (answer[6] == mine[index]) bonus++
        }

        var message = "-1"
        if (count == 6) message = "1"
        else if (count == 5 && bonus == 1) message = "2"
        else if (count == 5) message = "3"
        else if (count == 4) message = "4"
        else if (count == 3) message = "5"

        makeToast(message + "등")
    }
    private fun getColors():IntArray{
        return intArrayOf(
            //R.color.색깔name(color.xml파일 확인)
            ContextCompat.getColor(this, R.color.navigation_color_mood),
            ContextCompat.getColor(this, R.color.navigation_color_habit),
            ContextCompat.getColor(this, R.color.navigation_color_add),
            ContextCompat.getColor(this, R.color.navigation_color_chart),
            ContextCompat.getColor(this, R.color.navigation_color_profile)
        )
    }
}