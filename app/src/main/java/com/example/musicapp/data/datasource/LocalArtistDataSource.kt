package com.example.musicapp.data.datasource

import com.example.musicapp.domain.model.Artist

object LocalArtistDataSource {

    private val artists = mutableListOf(
        Artist(
            id = 1L,
            name = "Adele",
            imageUrl = "https://upload.wikimedia.org/wikipedia/vi/9/96/Adele_-_25_%28Official_Album_Cover%29.png",
            info = "Adele Laurie Blue Adkins, thường được biết đến với nghệ danh Adele, là một nữ ca sĩ kiêm nhạc sĩ sáng tác bài hát người Anh sinh ngày 5 tháng 5 năm 1988 tại Tottenham, London. Cô được toàn cầu công nhận nhờ chất giọng nội lực mạnh mẽ, sâu lắng và những bản ballad đầy cảm xúc, chủ yếu thuộc các dòng nhạc Pop, Soul và R&amp;B. Sự nghiệp của Adele bắt đầu bùng nổ vào năm 2008 với album đầu tay \"19\", nhưng phải đến album thứ hai \"21\" (2011) cô mới thực sự trở thành hiện tượng toàn cầu với những bản hit vượt thời gian như \"Rolling in the Deep\" và \"Someone Like You\". Cô tiếp tục củng cố vị thế của mình với các album \"25\" (2015) và \"30\" (2021). Với hàng loạt giải thưởng danh giá, bao gồm nhiều giải Grammy, Oscar và Brit Award, cùng lượng album bán ra hàng triệu bản, Adele được xem là một trong những nghệ sĩ có giọng hát xuất sắc nhất và có sức ảnh hưởng lớn nhất trong nền âm nhạc đương đại.",
            popularity = 90
        ),
        Artist(
            id = 2L,
            name = "The weeknd",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQV1S_pMTu7AKWkMi-pZc-lmflTsyqqQGcuUw&s",
            info = "The Weeknd, tên thật Abel Makkonen Tesfaye, là ca sĩ kiêm nhạc sĩ sản xuất thu âm người Canada sinh ngày 16 tháng 2 năm 1990. Anh được xem là một trong những nhân vật có ảnh hưởng nhất của nền âm nhạc đại chúng thế kỷ XXI, nổi tiếng với giọng nam cao pha trộn lối hát giả thanh (falsetto) và âm sắc độc đáo, cùng với phong cách âm nhạc kết hợp R&amp;B, pop và các yếu tố điện tử một cách tinh tế. Sinh ra ở Toronto trong gia đình có cha mẹ là người nhập cư Ethiopia và được nuôi dưỡng chủ yếu bởi mẹ và bà ngoại, Tesfaye bỏ học từ năm 17 tuổi để theo đuổi âm nhạc, đồng thời trải qua quãng thời gian \"buông thả\" với ma túy – những trải nghiệm này sau đó trở thành nguồn cảm hứng lớn cho các sáng tác đầy tâm trạng và mang tính tự truyện của anh. Sự nghiệp của anh khởi đầu một cách bí ẩn vào cuối năm 2009 khi những bài hát đầu tiên được đăng tải ẩn danh lên YouTube và nhanh chóng thu hút sự chú ý của làng nhạc, đặc biệt là từ rapper Drake – người sau này trở thành người ủng hộ và cộng tác quan trọng. Năm 2011, anh phát hành ba mixtape miễn phí mang tính bước ngoặt: House of Balloons, Thursday và Echoes of Silence, tạo nên một bộ ba (sau này được biên soạn lại thành album Trilogy năm 2012) và định hình nên thứ âm thanh R&amp;B thay thế tối tăm, gợi cảm, giúp anh trở thành hiện tượng trong giới underground.",
            popularity = 85
        ),
        Artist(
            id = 3L,
            name = "Taylor Swift",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTH1BgyjaN6eo1a8DzD5KE4rikj20LwG2wCmw&s",
            info = "Taylor Alison Swift (sinh ngày 13 tháng 12 năm 1989 tại West Reading, Pennsylvania, Hoa Kỳ) là một ca sĩ, nhạc sĩ người Mỹ được vinh danh là một trong những nhân vật có ảnh hưởng nhất trong văn hóa đại chúng đương đại nhờ khả năng kể chuyện qua âm nhạc, chiến lược tái sáng tạo nghệ thuật liên tục và thành công vượt trội về mặt thương mại. Sinh ra trong một gia đình có mẹ là Andrea (một giám đốc tiếp thị) và cha là Scott (một cố vấn tài chính), tuổi thơ của cô gắn liền với nông trại cây thông Noel ở Pennsylvania trước khi cả gia đình chuyển đến Nashville, Tennessee khi cô 14 tuổi để theo đuổi sự nghiệp âm nhạc. Cô bắt đầu được chú ý sau một buổi biểu diễn tại quán cà phê Bluebird Cafe năm 2005, dẫn đến hợp đồng thu âm với hãng đĩa Big Machine và album đầu tay mang tên \"Taylor Swift\" (2006) với phong cách country đã giúp cô trở thành hiện tượng mới trong làng nhạc đồng quê.",
            popularity = 80
        ),
        Artist(
            id = 4L,
            name = "Rihanna",
            imageUrl = "https://i.scdn.co/image/ab6761610000e5ebcb565a8e684e3be458d329ac",
            info = "Robyn Rihanna Fenty, được thế giới biết đến với nghệ danh Rihanna, sinh ngày 20 tháng 2 năm 1988 tại Saint Michael, Barbados, là một ca sĩ, nhạc sĩ, doanh nhân và là một biểu tượng văn hóa đa tài có tầm ảnh hưởng toàn cầu. Cô bước chân vào làng giải trí vào năm 2005 với album Music of the Sun, nhưng bước ngoặt đưa tên tuổi cô vươn tầm thế giới là album thứ ba Good Girl Gone Bad (2007) cùng hit \"Umbrella\", mang về cho cô giải Grammy đầu tiên. Trong sự nghiệp âm nhạc, Rihanna đã phát hành 8 album studio với hàng loạt bài hát quán quân, trở thành một trong những nghệ sĩ bán đĩa chạy nhất mọi thời đại với hơn 250 triệu bản thu âm, sở hữu 14 đĩa đơn vị trí số 1 trên bảng xếp hạng Billboard Hot 100. Cô được tôn vinh là Nghệ sĩ của thập kỷ (2010s) bởi Billboard và được vinh danh là Anh hùng Dân tộc của Barbados vào năm 2021.",
            popularity = 75
        ),
        Artist(
            id = 5L,
            name = "Lady Gaga",
            imageUrl = "https://www.rollingstone.com/wp-content/uploads/2024/07/GettyImages-1851907419.jpg?w=1581&h=1054&crop=1",
            info = "Stefani Joanne Angelina Germanotta, được thế giới biết đến với nghệ danh Lady Gaga, là một biểu tượng đa tài người Mỹ sinh ngày 28 tháng 3 năm 1986 tại thành phố New York, đã làm mới định nghĩa về một ngôi sao nhạc pop đương đại. Cô bắt đầu sự nghiệp âm nhạc vào năm 2008 với album \"The Fame\", nhanh chóng trở thành hiện tượng toàn cầu nhờ những bản hit như \"Just Dance\", \"Poker Face\" và \"Bad Romance\", không chỉ bởi giai điệu bắt tai mà còn nhờ phong cách thời trang phá cách và những màn trình diễn sân khấu đầy tính nghệ thuật. Sự nghiệp của cô tiếp tục ghi dấu ấn với album \"Born This Way\" (2011) cổ vũ cho sự đa dạng và tự chấp nhận bản thân, cho đến những dự án hợp tác đa dạng như album jazz với Tony Bennett và nhạc phim \"A Star Is Born\" (2018), qua đó cô đã giành được giải Oscar cho ca khúc nhạc phim xuất sắc nhất \"Shallow\". Ngoài âm nhạc, Lady Gaga còn được biết đến với vai diễn trong \"American Horror Story: Hotel\" (giành giải Quả cầu vàng) và \"House of Gucci\", cùng với những đóng góp tích cực cho hoạt động từ thiện thông qua quỹ Born This Way Foundation của mình. Với hơn 13 giải Grammy, 2 giải Quả cầu vàng, 1 giải Oscar và hàng triệu bản thu âm được tiêu thụ, Lady Gaga không chỉ là một nghệ sĩ giải trí mà còn là một biểu tượng văn hóa có tầm ảnh hưởng sâu rộng.",
            popularity = 70
        ),
        Artist(
            id = 6L,
            name = "Katy Perry",
            imageUrl = "https://hips.hearstapps.com/hmg-prod/images/katy-perry-workout-67597642423bb.jpg?crop=0.505xw:1.00xh;0,0&resize=640:*",
            info = "Katy Perry (tên thật Katheryn Elizabeth Hudson, sinh ngày 25 tháng 10, 1984 tại Santa Barbara, California) là một trong những nữ hoàng nhạc pop vĩ đại nhất và là nghệ sĩ bán đĩa chạy nhất mọi thời đại, với hơn 151 triệu bản thu âm được tiêu thụ toàn cầu. Cô bắt đầu sự nghiệp từ tuổi thơ nghiêm khắc trong một gia đình mục sư, chỉ được tiếp xúc với nhạc phúc âm, và phát hành một album thất bại thuộc thể loại này vào năm 2001 trước khi chuyển đến Los Angeles để theo đuổi nhạc pop. Bước ngoặt đưa Katy Perry trở thành ngôi sao quốc tế là năm 2008 với album \"One of the Boys\" và đĩa đơn gây sốt \"I Kissed a Girl\" – bản hit đầu tiên trong số 9 đĩa đơn quán quân Billboard của cô. Đỉnh cao của cô là album \"Teenage Dream\" (2010), một thành tựu vô song khi trở thành album đầu tiên và duy nhất của một nữ nghệ sĩ solo sản sinh ra 5 đĩa đơn quán quân tại Mỹ, sánh ngang kỷ lục của Michael Jackson. Những album tiếp theo như \"Prism\" (2013) với các hit \"Roar\" và \"Dark Horse\", hay các dự án mới nhất như \"143\" (2024), tiếp tục khẳng định vị thế của cô như một biểu tượng nhạc pop đầy sức ảnh hưởng với phong cách \"camp\" đặc trưng, được Vogue tôn vinh là \"Nữ hoàng Camp\".",
            popularity = 65
        ),
        Artist(
            id = 7L,
            name = "Justin Bieber",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTmTWAIpcAa48M1Try_nkxz56CljUGZVwFE1g&s",
            info = "Justin Drew Bieber (sinh ngày 1 tháng 3 năm 1994 tại London, Ontario, Canada) là một ca sĩ, nhạc sĩ người Canada, thường được xem là một trong những nghệ sĩ nhạc pop có ảnh hưởng nhất đầu thế kỷ XXI. Cuộc đời và sự nghiệp của anh là câu chuyện cổ tích hiện đại từ một cậu bé đam mê âm nhạc trên đường phố trở thành một siêu sao toàn cầu. Từ cậu bé 12 tuổi với những video cover đăng tải trên YouTube, Bieber đã được nhà quản lý Scooter Braun phát hiện, dẫn tới một cuộc gặp gỡ định mệnh với nam ca sĩ Usher và ký hợp đồng thu âm vào cuối năm 2008.",
            popularity = 60
        ),
        Artist(
            id = 8L,
            name = "Drake",
            imageUrl = "https://imageio.forbes.com/specials-images/imageserve/5ed578988b3c370006234c35/0x0.jpg?format=jpg&crop=1031,1031,x43,y49,safe&height=416&width=416&fit=bounds",
            info = "Drake (tên thật Aubrey Drake Graham) là rapper, ca sĩ, nhạc sĩ và diễn viên nổi tiếng người Canada, sinh ngày 24/10/1986 tại Toronto, được biết đến qua vai Jimmy Brooks trong Degrassi: The Next Generation trước khi trở thành siêu sao hip-hop với các hit như \"God's Plan\", \"Hotline Bling\", nổi tiếng với sự pha trộn giữa rap và hát R&B, khám phá chủ đề tình cảm, và gặt hái nhiều giải thưởng lớn.",
        ),
        Artist(
            id = 9L,
            name = "Bruno Mars",
            imageUrl = "https://photo-resize-zmp3.zmdcdn.me/w240_r1x1_jpeg/avatars/f/c/8/5/fc85640aa7ff9a8f27a0d7d8aa449ce2.jpg",
            info = "Bruno Mars (tên thật Peter Gene Hernandez, sinh 1985) là ca sĩ, nhạc sĩ, nhà sản xuất và nhạc công người Mỹ gốc Philippines nổi tiếng với phong cách nhạc pop, R&B, funk, soul có pha lẫn yếu tố cổ điển, thường có màn trình diễn live sôi động, được hỗ trợ bởi ban nhạc The Hooligans. Anh ra mắt với album Doo-Wops & Hooligans (2010) và thành công với các hit như \"Just the Way You Are\", \"Uptown Funk\", \"That's What I Like\", giành nhiều giải Grammy và có hơn 180 triệu đĩa đơn bán ra toàn cầu, đồng thời là thành viên của bộ đôi Silk Sonic.",
        ),
        Artist(
            id = 10L,
            name = "Ariana Grande",
            imageUrl = "https://ss-images.saostar.vn/2020/05/12/7479191/ariana-grande-1.jpg",
            info = "Ariana Grande-Butera (sinh ngày 26 tháng 6 năm 1993) là một ca sĩ, nhạc sĩ và diễn viên người Mỹ, được mệnh danh là \"nữ hoàng nhạc pop\" nhờ chất giọng nội lực mạnh mẽ với quãng giọng rộng hơn 4 quãng tám và tầm ảnh hưởng lớn đến văn hóa đại chúng. Cô khởi đầu sự nghiệp với vai diễn Cat Valentine trong các series nổi tiếng của kênh Nickelodeon như Victorious và Sam &amp; Cat trước khi chuyển hướng sang âm nhạc và gặt hái thành công vang dội. Album phòng thu đầu tay Yours Truly (2013) của cô đã ra mắt ở vị trí số một trên bảng xếp hạng Billboard 200, mở đường cho một loạt album đình đám sau này như My Everything (2014), Dangerous Woman (2016), Sweetener (2018), Thank U, Next (2019) - album chứa hai đĩa đơn quán quân liên tiếp phá kỷ lục, Positions (2020) và Eternal Sunshine (2024). Với 9 đĩa đơn và 6 album đạt vị trí số một trên Billboard, cùng nhiều giải thưởng danh giá bao gồm 2 giải Grammy, Ariana Grande đã khẳng định vị thế hàng đầu của mình trong làng nhạc đương đại, không chỉ qua những bản hit mà còn bởi phong cách thời trang biểu tượng và những đóng góp tích cực cho các hoạt động từ thiện, cũng như việc sáng tạo thương hiệu mỹ phẩm REM Beauty thành công.",
        )
    )

    fun getAllArtists(): List<Artist> = artists

    fun getArtistById(artistId: Long): Artist? {
        return artists.find { it.id == artistId }
    }

    fun getTrendingArtists(limit: Int = 5): List<Artist> {
        return artists
            .sortedByDescending { it.popularity }
            .take(limit)
    }

    fun updateArtist(
        artistId: Long,
        transform: (Artist) -> Artist
    ) {
        val index = artists.indexOfFirst { it.id == artistId }
        if(index != -1) {
            artists[index] = transform(artists[index])
        }
    }

    fun searchArtists(query: String): List<Artist> {
        return artists.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

}