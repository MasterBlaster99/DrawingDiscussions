package com.example.drawingdiscussions.utils

class Utils {
    companion object {
        fun getTimeAgo(timestamp: Long): String {
            val currentTime = System.currentTimeMillis()
            val timeDifference = currentTime - timestamp

            val seconds = timeDifference / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val weeks = days / 7
            val months = days / 30
            val years = days / 365

            return when {
                years > 0 -> "$years ${if (years == 1L) "year" else "years"} ago"
                months > 0 -> "$months ${if (months == 1L) "month" else "months"} ago"
                weeks > 0 -> "$weeks ${if (weeks == 1L) "week" else "weeks"} ago"
                days > 0 -> "$days ${if (days == 1L) "day" else "days"} ago"
                hours > 0 -> "$hours ${if (hours == 1L) "hour" else "hours"} ago"
                minutes > 0 -> "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
                else -> "$seconds ${if (seconds == 1L) "second" else "seconds"} ago"
            }
        }

    }
}