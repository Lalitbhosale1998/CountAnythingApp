package com.lalit.countanything.ui.models

sealed class CalendarDay(val day: Int?)
class EmptyDay : CalendarDay(null)
class MonthDay(day: Int) : CalendarDay(day)
