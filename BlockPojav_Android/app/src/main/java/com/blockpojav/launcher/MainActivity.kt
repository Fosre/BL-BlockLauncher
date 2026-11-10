package com.blockpojav.launcher

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : Activity() {
    private val bg = Color.rgb(10, 12, 16)
    private val panel = Color.rgb(23, 27, 35)
    private val panel2 = Color.rgb(31, 37, 47)
    private val accent = Color.rgb(83, 190, 255)
    private val white = Color.rgb(240, 244, 250)
    private val muted = Color.rgb(155, 166, 182)
    private lateinit var root: LinearLayout
    private lateinit var versionSpinner: Spinner
    private lateinit var nickname: EditText
    private var accountMode = "Офлайн"
    private val versions = listOf("1.12", "1.12.1", "1.12.2", "1.16.5", "1.17.1", "1.18.2", "1.19.4", "1.20.1", "1.20.4", "1.20.6", "1.21", "1.21.1", "1.21.4", "1.21.5", "1.21.8", "Указать версию вручную…")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        showHome()
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun rounded(color: Int, radius: Int = 18): GradientDrawable = GradientDrawable().apply { setColor(color); cornerRadius = dp(radius).toFloat() }
    private fun text(s: String, size: Float, color: Int = white, bold: Boolean = false) = TextView(this).apply {
        text = s; textSize = size; setTextColor(color); if (bold) typeface = Typeface.DEFAULT_BOLD
    }
    private fun button(s: String, filled: Boolean = false, action: () -> Unit) = Button(this).apply {
        text = s; textSize = 14f; isAllCaps = false; setTextColor(if (filled) Color.BLACK else white)
        background = rounded(if (filled) accent else panel2, 14); setOnClickListener { action() }
    }
    private fun card(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL; setPadding(dp(16), dp(15), dp(16), dp(15)); background = rounded(panel); elevation = dp(2).toFloat()
    }
    private fun addGap(parent: LinearLayout, h: Int = 12) { parent.addView(View(this), LinearLayout.LayoutParams(1, dp(h))) }
    private fun showHome() {
        val scroll = ScrollView(this).apply { setBackgroundColor(bg) }
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(18), dp(20), dp(28)); setBackgroundColor(bg) }
        scroll.addView(root); setContentView(scroll)
        root.addView(text("BLOCKPOJAV", 13f, accent, true))
        root.addView(text("Твой Minecraft. Твои правила.", 25f, white, true))
        root.addView(text("Java Edition launcher для Android", 13f, muted))
        addGap(root, 20)

        val profile = card()
        profile.addView(text("АККАУНТ", 11f, accent, true))
        addGap(profile, 8)
        val modes = Spinner(this).apply {
            adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, listOf("Офлайн-аккаунт", "Microsoft account"))
            setSelection(0)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    accountMode = if (position == 0) "Офлайн" else "Microsoft"
                    nickname.hint = if (position == 0) "Имя игрока (3–16 символов)" else "Имя профиля"
                }
            }
        }
        profile.addView(modes)
        nickname = EditText(this).apply { hint = "Имя игрока (3–16 символов)"; setHintTextColor(muted); setTextColor(white); textSize = 15f; setSingleLine(true); background = rounded(panel2, 12); setPadding(dp(12), dp(10), dp(12), dp(10)) }
        addGap(profile, 8); profile.addView(nickname)
        addGap(profile, 8)
        profile.addView(button("Подключить Microsoft", false) {
            AlertDialog.Builder(this).setTitle("Вход Microsoft").setMessage("Безопасный вход Microsoft ещё не подключён в этой сборке. Не вводи пароль в сторонние формы.").setPositiveButton("Понятно", null).show()
        })
        root.addView(profile)
        addGap(root)

        val game = card()
        game.addView(text("ВЕРСИЯ ИГРЫ", 11f, accent, true)); addGap(game, 8)
        versionSpinner = Spinner(this).apply { adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, versions) }
        game.addView(versionSpinner)
        game.addView(text("Можно выбрать доступную версию или указать другую вручную. Наличие версии проверяется при подключении загрузчика.", 12f, muted))
        addGap(game, 10)
        game.addView(button("Указать версию вручную", false) {
            val input = EditText(this).apply { hint = "Например, 1.20.4"; setSingleLine(true) }
            AlertDialog.Builder(this).setTitle("Версия Minecraft Java").setView(input).setNegativeButton("Отмена", null).setPositiveButton("Сохранить") { _, _ ->
                val value = input.text.toString().trim()
                if (value.matches(Regex("[0-9]+\\.[0-9]+(\\.[0-9]+)?(-[A-Za-z0-9._-]+)?"))) {
                    val adapter = versionSpinner.adapter as ArrayAdapter<String>
                    if (adapter.getPosition(value) < 0) { adapter.add(value); adapter.notifyDataSetChanged() }
                    versionSpinner.setSelection(adapter.getPosition(value))
                } else Toast.makeText(this, "Проверь формат версии", Toast.LENGTH_SHORT).show()
            }.show()
        })
        root.addView(game)
        addGap(root)

        val runtime = card()
        runtime.addView(text("JAVA RUNTIME", 11f, accent, true)); addGap(runtime, 6)
        runtime.addView(text("Среда Java: ещё не установлена", 16f, white, true))
        runtime.addView(text("Автоматическая загрузка Java и игровых библиотек будет добавлена вместе с настоящим игровым ядром. Сейчас это макет интерфейса.", 12f, muted))
        root.addView(runtime)
        addGap(root)

        val settings = card()
        settings.addView(text("ПРОИЗВОДИТЕЛЬНОСТЬ", 11f, accent, true)); addGap(settings, 8)
        settings.addView(text("ОЗУ для игры: 2 ГБ", 15f, white, true))
        val ram = SeekBar(this).apply { max = 6; progress = 1; progressTintList = android.content.res.ColorStateList.valueOf(accent); thumbTintList = android.content.res.ColorStateList.valueOf(accent) }
        settings.addView(ram)
        ram.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p: Int, fromUser: Boolean) { (settings.getChildAt(1) as TextView).text = "ОЗУ для игры: ${p + 1} ГБ" }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        settings.addView(text("Это пока настройка макета, реальное выделение памяти появится после интеграции runtime.", 11f, muted))
        root.addView(settings)
        addGap(root, 18)
        root.addView(button("ИГРАТЬ", true) {
            val name = nickname.text.toString().trim()
            if (accountMode == "Офлайн" && !name.matches(Regex("[A-Za-z0-9_]{3,16}"))) {
                Toast.makeText(this, "Для офлайн-профиля укажи имя 3–16 символов: латиница, цифры или _", Toast.LENGTH_LONG).show()
            } else {
                AlertDialog.Builder(this).setTitle("Игровое ядро ещё не подключено").setMessage("Интерфейс готов как прототип, но Minecraft Java пока не запускается. Для полноценной игры нужны совместимый runtime, загрузчик версий, библиотеки и официальный вход Microsoft для лицензированной игры.").setPositiveButton("Понятно", null).show()
            }
        })
        addGap(root, 12)
        root.addView(text("BLOCKPOJAV • UI prototype", 11f, muted).apply { gravity = Gravity.CENTER })
    }
}
