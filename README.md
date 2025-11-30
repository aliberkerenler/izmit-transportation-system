# İZMİT ULAŞIM ROTA PLANLAMA SİSTEMİ

## 👥 Proje Sahipleri
* Ömer Faruk Toycu (@omertoycu)
* Ali Berke Erenler (@aliberkerenler)

---

## 🎯 Proje Amacı
Bu Java tabanlı proje, Kocaeli'nin İzmit ilçesi için geliştirilmiş bir toplu taşıma ve taksi rota planlama sistemidir. Temel amaç, kullanıcının mevcut konumundan (enlem/boylam) hedef konumuna en uygun (maliyet, süre, mesafe) rotayı Nesne Yönelimli Programlama (OOP) prensiplerini kullanarak bulmaktır.

## 🛠️ Teknolojiler ve Kütüphaneler
* **Dil:** Java
* **Veri Yapısı:** Grafik (Graph) yapısı (Otobüs ve Tramvay hatları için).
* **Kütüphane:** Gson (JSON veri işleme için)
* **Algoritma:** Dijkstra (En kısa yolu maliyet, süre veya mesafeye göre bulmak için).
* **Mesafe Hesaplama:** Haversine formülü (Enlem/boylam arasındaki mesafeyi bulmak için).

---

## 🏗️ Nesne Yönelimli Tasarım (OOP)
Proje, OOP prensiplerine sıkı sıkıya bağlı kalınarak tasarlanmıştır.

| Kategori | Temel Sınıf / Arayüz | Alt Sınıflar (Çok Biçimlilik) | Amaç |
| :--- | :--- | :--- | :--- |
| **Yolcu Tipi** | `Passenger` (Soyut Sınıf) | `GeneralPassenger`, `StudentPassenger`, `ElderlyPassenger` | Farklı yolcu tiplerine göre ücret indirimlerini ve kurallarını (örn. 65+ yaş sınırlamaları) uygulamak. |
| **Ödeme Yöntemi** | `Payment` (Arayüz/Soyut) | `CashPayment`, `CreditCardPayment`, `KentkartPayment` | Yolculuk maliyetinin seçilen ödeme yöntemiyle (limit/bakiye) karşılanıp karşılanamayacağını kontrol etmek. |
| **Ulaşım Aracı** | `Vehicle` (Soyut Sınıf) | `Bus`, `Tram`, `Taxi`, `Transfer` | Her ulaşım modunun (Otobüs, Tramvay, Taksi) kendi ücret ve süre hesaplama mantığını sağlamak. |

---

## ✨ Temel Özellikler
* **Entegre Rota Planlama:** Otobüs, tramvay ve taksi seçeneklerini birlikte değerlendirerek en uygun rotayı sunar.
* **Taksi Zorunluluğu Kuralı:** Kullanıcının başlangıç veya hedef konumu, en yakın durağa 3 km'den (eşik değer) daha uzaksa, bu mesafeyi katetmek için taksi kullanımı zorunlu tutulur.
* **Detaylı Rota Alternatifleri:** En uygun rotanın yanı sıra, **Sadece Otobüs**, **Sadece Taksi**, **Aktarmalı** gibi alternatif rotaları da hesaplar ve karşılaştırır.
* **Kişiselleştirilmiş Ücretlendirme:** Yolcu tipine göre (Öğrenci, 65 Yaş Üstü vb.) indirimler uygulanır ve seçilen ödeme yönteminin (Nakit, Kentkart, Kredi Kartı) yeterliliği kontrol edilir.

## 🚀 Çalıştırma Talimatları
1.  Projeyi bir Java geliştirme ortamında (örneğin IntelliJ veya Maven/Gradle projesi olarak) açın.
2.  `Main.java` sınıfını çalıştırın.
3.  Uygulama, konsol üzerinden sırasıyla başlangıç ve bitiş enlem/boylam koordinatlarını, yolcu tipini ve ödeme bilgilerini isteyecektir.
4.  Girdiler tamamlandıktan sonra, bulunan tüm rota alternatifleri detaylı maliyet ve süre bilgileriyle birlikte konsola yazdırılacaktır.
