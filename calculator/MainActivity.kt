package com.example.hdev.calculator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    val TAG: String = "MainActivity"!!
    val MESSAGE = "Biểu thức không đúng"!!
    val ADD = '+'!!
    val SUB = '-'!!
    val DIV = '/'!!
    val MUL = 'x'!!
    val PERCENT = '%'!!
    val O_BRACKETS = '('!!
    val C_BRACKETS = ')'!!

    private var mExpression: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(v: View) {
        when (v.id) {
            R.id.button_del -> deleteChar()
            R.id.button_del_all -> {
                mExpression = ""
                text_calculation?.setText("")
                text_result?.setText("")
            }
            R.id.button_result -> {
                mExpression = text_calculation?.text.toString()
                if (check()) {
                    text_result?.setText(infixToPostfix())
                } else {
                    Toast.makeText(this, MESSAGE, Toast.LENGTH_LONG)
                }
            }

            R.id.button_extends -> {
                // Mo rong phep toan
            }

            R.id.button_0, R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4,
            R.id.button_5, R.id.button_6, R.id.button_7, R.id.button_dupZero,
            R.id.button_8, R.id.button_9, R.id.button_div, R.id.button_mutil,
            R.id.button_add, R.id.button_dot, R.id.button_sub -> {
                mExpression += v.tag
                text_calculation?.setText(mExpression)
                text_result?.setText("")
            }
        }
    }

    fun deleteChar() {
        if (mExpression?.length!! > 1) {
            mExpression = mExpression?.substring(0, mExpression!!.length - 1)
        } else {
            mExpression = ""
            text_result?.setText("")
        }
        text_calculation?.setText(mExpression)
    }

    fun check(): Boolean {
        val c: Char? = mExpression?.get(mExpression?.length!! - 1)
        if (c == ADD || c == SUB || c == MUL || c == DIV) return false
        return true
    }

    fun convertToStringArray(): ArrayList<String> {
        var arr: ArrayList<String>
        var sBuilder = StringBuilder()
        for (i in 0 until mExpression?.length!!) {
            var c = mExpression!![i]
            if (!isOperator(c)) {
                sBuilder.append(c)
            } else {
                sBuilder.append(" " + c + " ")
            }
        }
        var stringResult = sBuilder.toString()
        stringResult = stringResult.trim()
        stringResult.replace("  ", " ")
        arr = stringResult.split(" ") as ArrayList<String>
        return arr
    }

    fun infixToPostfix(): String {
        var inFix: ArrayList<String> = convertToStringArray()
        var sBuilder = StringBuilder()
        var outPut: ArrayList<String>
        var stack: Stack<String> = Stack()

        for (i in 0 until inFix.size) {
            var c = inFix[i].get(0)
            if (!isOperator(c)) {
                sBuilder.append(" " + inFix[i])
            } else {
                if (c == O_BRACKETS) {
                    stack.push(inFix[i])
                } else {
                    if (c == C_BRACKETS) {
                        var c1: Char
                        do {
                            c1 = stack.peek().get(0)
                            if (c1 != O_BRACKETS) sBuilder.append(" " + stack.peek())
                            stack.pop()
                        } while (c1 != O_BRACKETS)
                    } else {
                        while (!stack.isEmpty() && getPriority(stack.peek().get(0)) >= getPriority(c)) {
                            sBuilder.append(" " + stack.peek())
                            stack.pop()
                        }
                        stack.push(inFix[i])
                    }
                }
            }
        }
        while (!stack.isEmpty()) {
            sBuilder.append(" " + stack.peek())
            stack.pop()
        }
        var stringResult = sBuilder.toString()
        stringResult = stringResult.trim()
        outPut = stringResult.split(" ") as ArrayList<String>
        return calculate(outPut)
    }

    fun calculate(inPut: ArrayList<String>): String {
        var stack: Stack<String> = Stack()
        for (i in 0 until inPut.size) {
            var c = inPut[i].get(0)
            if (!isOperator(c)) {
                stack.push(inPut[i])
            } else {
                var num = 0.0
                var num1: Double = stack.pop().toDouble()
                var num2: Double = stack.pop().toDouble()
                when (c) {
                    ADD -> num = num2 + num1
                    SUB -> num = num2 - num1
                    MUL -> num = num2 * num1
                    DIV -> {
                        if (num1.equals(0)) {
                            Toast.makeText(this, MESSAGE, Toast.LENGTH_SHORT)
                            return ""
                        }
                        num = num2 / num1
                    }
                }
                stack.push(num.toString())
            }
        }
        return stack.pop()
    }

    // Do uu tien
    fun getPriority(char: Char): Int {
        if (char == MUL || char == DIV || char == PERCENT) return 2;
        if (char == ADD || char == SUB) return 1
        return 0
    }

    // Kiem tra la toan tu
    fun isOperator(c: Char): Boolean {
        val operator: CharArray = charArrayOf(ADD, SUB, MUL, DIV, O_BRACKETS, C_BRACKETS)
        Arrays.sort(operator)
        if (Arrays.binarySearch(operator, c) > -1) return true
        return false
    }
}
