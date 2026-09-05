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
        AdobeMarketoEngage,
        Aliexpress,
        AmazonProduct,
        Amazon,
        AmazonSponsoredProduct,
        AolSearch,
        AtAnalytics,
        AutoTrader,
        AvantLink,
        Bilibili,
        BlueskyRedirect,
        CarGurus,
        Change,
        CxAnalytics,
        Dianping,
        Dingtalk,
        Douyin,
        Ebay,
        Echobox,
        ElFinanciero,
        EmptyParameters,
        FacebookAnalytics,
        Facebook,
        FacebookRedirect,
        FastCompany,
        Feishu,
        Flipkart,
        GeoRiot,
        GoogleAds,
        GoogleAnalytics,
        GoogleMaps,
        GoogleSearch,
        GoogleStore,
        Heise,
        HumbleBundle,
        Ikea,
        IlMessaggero,
        IlSole24Ore,
        Instagram,
        Jd,
        Jdoqocy,
        Jodel,
        Kogan,
        Kuaishou,
        LatinaToday,
        Lazada,
        Liberation,
        LinkSynergy,
        LinkedIn,
        Meituan,
        MetaAd,
        MyDealzParameters,
        MyDealzRedirects,
        Netflix,
        NewEgg,
        Pdd,
        Pearl,
        RedditMail,
        RedditOut,
        Reddit,
        SalesforceParameters,
        SessionIds,
        Shopee,
        Snapchat,
        Spiegel,
        Spotify,
        Substack,
        Taobao,
        TheGuardian,
        Threads,
        Tiktok,
        Webtrekk,
        Wechat,
        Weibo,
        Wikipedia,
        X,
        Xiaohongshu,
        YahooReferrer,
        YahooSearch,
        Yandex,
        YoutubeMusic,
        YoutubeRedirect,
        Youtube,
        YoutubeShortUrl,
        Zhihu,
    )
