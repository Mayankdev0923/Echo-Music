package iad1tya.echo.music.akai.commitscreen

data class CommitData(
    val message: String,
    val authorName: String,
    val date: String,
    val sha: String,
    val authorAvatarUrl: String?
)
