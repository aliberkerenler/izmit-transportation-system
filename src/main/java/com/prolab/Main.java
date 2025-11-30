package com.prolab;

import com.prolab.graph.Graph;
import com.prolab.model.Route;
import com.prolab.model.RouteSegment;
import com.prolab.model.GeneralPassenger;
import com.prolab.model.StudentPassenger;
import com.prolab.model.ElderlyPassenger;
import com.prolab.model.Passenger;
import com.prolab.model.CashPayment;
import com.prolab.model.CreditCardPayment;
import com.prolab.model.KentkartPayment;
import com.prolab.service.DataLoader;
import com.prolab.service.RouteService;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            // Veri yükleyiciyi oluştur
            DataLoader dataLoader = new DataLoader();

            // JSON dosyasından verileri yükle
            String dataPath = "ProLab/src/main/resources/data/veriseti.json";
            System.out.println("📂 Dosya yükleniyor: data/veriseti.json");
            Graph graph = dataLoader.loadData(dataPath);
            Map<String, Double> taxiInfo = dataLoader.loadTaxiInfo(dataPath);
            System.out.println("✅ Dosya başarıyla yüklendi!\n");

            // Rota servisini oluştur
            RouteService routeService = new RouteService(
                    graph,
                    taxiInfo.get("openingFee"),
                    taxiInfo.get("costPerKm")
            );

            // Kullanıcıdan bilgileri al
            Scanner scanner = new Scanner(System.in);

            System.out.println("🚌 İzmit Toplu Taşıma ve Taksi Sistemi 🚕");
            System.out.println("----------------------------------------\n");


            System.out.println("\uD83D\uDCCDDurak Koordinatları:");
            System.out.println("   🚌 OTOBÜS DURAKLARI:\n");
            System.out.println("   🚌 Otogar: 40.78259, 29.94628");
            System.out.println("   🚌 Sekapark: 40.76520, 29.96190");
            System.out.println("   🚌 Umuttepe: 40.82103, 29.91843");
            System.out.println("   🚌 Yahya Kaptan: 40.770965, 29.959499");
            System.out.println("   🚌 41 Burda: 40.77731, 29.92512");
            System.out.println("   🚌 Symbol AVM: 40.77788, 29.94991\n");
            System.out.println("   🚋 TRAMVAY DURAKLARI:\n");
            System.out.println("   🚋 Otogar: 40.78259, 29.94628");
            System.out.println("   🚋 Sekapark: 40.76520, 29.96190");
            System.out.println("   🚋 Yahya Kaptan: 40.770965, 29.959499");
            System.out.println("   🚋 Halkevi: 40.76350, 29.93870\n");

            System.out.println("  İpucu: Koordinatları nokta (.) kullanarak girin.");
            System.out.println("   Örnek: 40.78259\n");


            System.out.println("📍 Başlangıç Noktası");
            System.out.println("  Başlangıç koordinatlarını girin:\n");

            System.out.print("🌐 Enlem: ");
            double startLat = Double.parseDouble(scanner.nextLine());

            System.out.print("\n🌐 Boylam: ");
            double startLon = Double.parseDouble(scanner.nextLine());


            System.out.println("\n🎯 Bitiş Noktası");
            System.out.println("  Bitiş koordinatlarını girin:\n");

            System.out.print("🌐 Enlem: ");
            double endLat = Double.parseDouble(scanner.nextLine());

            System.out.print("\n🌐 Boylam: ");
            double endLon = Double.parseDouble(scanner.nextLine());


            System.out.println("\n👥 Yolcu Bilgileri:");
            System.out.println("1. 👤 Genel (Tam Bilet)");
            System.out.println("2. 🎓 Öğrenci");
            System.out.println("3. 👴 65 Yaş Üstü");

            int passengerType = 0;
            while (passengerType < 1 || passengerType > 3) {
                System.out.print("\nSeçiminiz (1-3): ");
                String input = scanner.nextLine().trim();
                try {
                    passengerType = Integer.parseInt(input);
                    if (passengerType < 1 || passengerType > 3) {
                        System.out.println("❌ Lütfen 1 ile 3 arasında bir sayı giriniz.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("❌ Lütfen geçerli bir sayı giriniz.");
                }
            }

            Passenger passenger;
            switch (passengerType) {
                case 1:
                    passenger = new GeneralPassenger();
                    break;
                case 2:
                    passenger = new StudentPassenger();
                    break;
                case 3:
                    passenger = new ElderlyPassenger();
                    break;
                default:
                    passenger = new GeneralPassenger();
                    break;
            }


            System.out.println("\n💳 Ödeme Bilgileri:");
            System.out.println("  Her ödeme yöntemi için limit giriniz:");

            System.out.print("💰 Nakit Miktarı (TL): ");
            double cashAmount = Double.parseDouble(scanner.nextLine());
            CashPayment cashPayment = new CashPayment(cashAmount);

            System.out.print("💳 Kredi Kartı Limiti (TL): ");
            double creditCardLimit = Double.parseDouble(scanner.nextLine());
            CreditCardPayment creditCardPayment = new CreditCardPayment(creditCardLimit);

            System.out.print("🎫 KentKart Bakiyesi (TL): ");
            double kentCardBalance = Double.parseDouble(scanner.nextLine());
            KentkartPayment kentkartPayment = new KentkartPayment(kentCardBalance);


            List<Route> routes = routeService.findRoutes(startLat, startLon, endLat, endLon, passenger);


            System.out.println("\n🚌 Bulunan Rotalar:\n");
            for (int i = 0; i < routes.size(); i++) {
                Route route = routes.get(i);
                System.out.println("\uD83D\uDEE3\uFE0F Rota " + (i + 1) + ":");


                String routeType = "";
                boolean hasBus = false;
                boolean hasTram = false;
                boolean hasTaxi = false;

                for (RouteSegment segment : route.getSegments()) {
                    switch (segment.getType()) {
                        case "bus":
                            hasBus = true;
                            break;
                        case "tram":
                            hasTram = true;
                            break;
                        case "taxi":
                            hasTaxi = true;
                            break;
                    }
                }

                if (hasTaxi && !hasBus && !hasTram) {
                    routeType = "🚕 Sadece Taksi";
                } else if (hasBus && !hasTram && !hasTaxi) {
                    routeType = "🚌 Sadece Otobüs";
                } else if (hasTram && !hasBus && !hasTaxi) {
                    routeType = "🚋 Sadece Tramvay";
                } else if (hasBus && hasTram) {
                    routeType = "🔄 Otobüs + Tramvay";
                } else if (hasTaxi && hasBus) {
                    routeType = "🚕 Taksi + Otobüs";
                } else if (hasTaxi && hasTram) {
                    routeType = "🚕 Taksi + Tramvay";
                }

                System.out.println("📋 Rota Tipi: " + routeType);
                System.out.println(route);


                System.out.println("\n💳 Ödeme Seçenekleri:");
                if(cashPayment.canPay(route.getTotalCost())) {
                    System.out.println("✅ Nakit ile ödeme yapılabilir");
                } else {
                    System.out.println("❌ Nakit Yetersiz!");
                }

                if(creditCardPayment.canPay(route.getTotalCost())) {
                    System.out.println("✅ Kredi kartı ile ödeme yapılabilir");
                } else {
                    System.out.println("❌ Kredi Kartı Limiti Yetersiz!");
                }

                if(kentkartPayment.canPay(route.getTotalCost())) {
                    System.out.println("✅ KentKart ile ödeme yapılabilir");
                } else {
                    System.out.println("❌ Kentkart Bakiyesi Yetersiz!");
                }
                System.out.println();
            }

            scanner.close();

        } catch (IOException e) {
            System.err.println("❌ Veri yükleme hatası: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("❌ Hata: Lütfen geçerli sayısal değerler giriniz.");
            e.printStackTrace();
        }
    }
}