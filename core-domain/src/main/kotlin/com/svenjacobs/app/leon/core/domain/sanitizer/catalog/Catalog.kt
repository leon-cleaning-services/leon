/*
 * Léon - The URL Cleaner
 * Copyright (C) 2026 Sven Jacobs
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.svenjacobs.app.leon.core.domain.sanitizer.catalog

import com.svenjacobs.app.leon.core.domain.sanitizer.SanitizersCollection
import kotlinx.collections.immutable.persistentListOf

/**
 * Every sanitizer Léon knows.
 *
 * Adding one means adding a [Sanitizer][com.svenjacobs.app.leon.core.domain.sanitizer.Sanitizer] to
 * its vendor's file in this package and one entry to this list.
 */
val AllSanitizers: SanitizersCollection =
    persistentListOf(
        AdobeMarketoEngageSanitizer,
        AliexpressSanitizer,
        AmazonProductSanitizer,
        AmazonSanitizer,
        AolSearchSanitizer,
        AtAnalyticsSanitizer,
        AutoTraderSanitizer,
        BilibiliSanitizer,
        BlueskyRedirectSanitizer,
        CarGurusSanitizer,
        ChangeSanitizer,
        CxAnalyticsSanitizer,
        DianpingSanitizer,
        DingtalkSanitizer,
        DouyinSanitizer,
        EbaySanitizer,
        EchoboxSanitizer,
        ElFinancieroSanitizer,
        EmptyParametersSanitizer,
        FacebookAnalyticsSanitizer,
        FacebookSanitizer,
        FastCompanySanitizer,
        FeishuSanitizer,
        FlipkartSanitizer,
        GeoRiotSanitizer,
        GoogleAdsSanitizer,
        GoogleAnalyticsSanitizer,
        GoogleMapsSanitizer,
        GoogleSearchSanitizer,
        GoogleStoreSanitizer,
        HeiseSanitizer,
        IkeaSanitizer,
        IlMessaggeroSanitizer,
        InstagramSanitizer,
        JdSanitizer,
        JdoqocySanitizer,
        JodelSanitizer,
        KoganSanitizer,
        KuaishouSanitizer,
        LatinaTodaySanitizer,
        LazadaSanitizer,
        LinkSynergySanitizer,
        LinkedInSanitizer,
        MeituanSanitizer,
        MetaAdSanitizer,
        MyDealzParametersSanitizer,
        MyDealzRedirectsSanitizer,
        NetflixSanitizer,
        NewEggSanitizer,
        PddSanitizer,
        PearlSanitizer,
        RedditMailSanitizer,
        RedditOutSanitizer,
        RedditSanitizer,
        SalesforceParametersSanitizer,
        SessionIdsSanitizer,
        ShopeeSanitizer,
        SnapchatSanitizer,
        SpiegelSanitizer,
        SpotifySanitizer,
        SubstackSanitizer,
        TaobaoSanitizer,
        TheGuardianSanitizer,
        ThreadsSanitizer,
        TiktokSanitizer,
        WebtrekkSanitizer,
        WechatSanitizer,
        WeiboSanitizer,
        WikipediaSanitizer,
        XSanitizer,
        XiaohongshuSanitizer,
        YahooReferrerSanitizer,
        YahooSearchSanitizer,
        YandexSanitizer,
        YoutubeMusicSanitizer,
        YoutubeRedirectSanitizer,
        YoutubeSanitizer,
        YoutubeShortUrlSanitizer,
        ZhihuSanitizer,
    )
