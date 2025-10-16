package dk.ek.kinoxp.service;

import dk.ek.kinoxp.domain.Item;
import dk.ek.kinoxp.domain.Sale;
import dk.ek.kinoxp.domain.SaleLine;
import dk.ek.kinoxp.domain.Screening;
import dk.ek.kinoxp.domain.Ticket;
import dk.ek.kinoxp.domain.enums.TicketType;
import dk.ek.kinoxp.repository.ItemRepository;
import dk.ek.kinoxp.repository.SaleRepository;
import dk.ek.kinoxp.repository.ScreeningRepository;
import dk.ek.kinoxp.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CombinedSaleService {

    private final SaleRepository saleRepo;
    private final ScreeningRepository screeningRepo;
    private final TicketRepository ticketRepo;
    private final ItemRepository itemRepo;

    public CombinedSaleService(SaleRepository saleRepo,
                               ScreeningRepository screeningRepo,
                               TicketRepository ticketRepo,
                               ItemRepository itemRepo) {
        this.saleRepo = saleRepo;
        this.screeningRepo = screeningRepo;
        this.ticketRepo = ticketRepo;
        this.itemRepo = itemRepo;
    }

    @Transactional
    public Sale registerCombinedSale(Long screeningId,
                                     Map<TicketType, Integer> ticketQuantities,
                                     Map<Long, Integer> itemQuantities) {

        Screening screening = screeningRepo.findById(screeningId).orElseThrow();
        Sale sale = new Sale();
        int total = 0;

        // --- Billetter ---
        for (Map.Entry<TicketType, Integer> entry : ticketQuantities.entrySet()) {
            TicketType type = entry.getKey();
            int qty = entry.getValue() == null ? 0 : entry.getValue();
            if (qty <= 0) continue;

            for (int i = 0; i < qty; i++) {
                if (screening.getAvailableSeats() <= 0) {
                    throw new IllegalStateException("Ingen ledige pladser tilbage.");
                }

                Ticket t = new Ticket();
                t.setScreening(screening);
                t.setTicketType(type);          // <-- ændret fra setType(...)
                t.setSale(sale);

                // TODO: sæt rigtige pladser hvis I har sædevalg; midlertidigt eksempel:
                t.setRowLetter("A");
                t.setSeatNumber(1 + i); // undgå duplikerede sæder i eksemplet

                ticketRepo.save(t);

                // Brug filmens pris (Ticket har ikke price-felt)
                int unitPrice = screening.getMovie().getPrice();
                total += unitPrice;

                screening.setAvailableSeats(screening.getAvailableSeats() - 1);
            }
        }
        screeningRepo.save(screening);

        // --- Kioskvarer ---
        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            Long itemId = entry.getKey();
            int qty = entry.getValue() == null ? 0 : entry.getValue();
            if (qty <= 0) continue;

            Item item = itemRepo.findById(itemId).orElseThrow();
            int lineTotal = item.getPrice() * qty;

            SaleLine line = new SaleLine();
            line.setSale(sale);
            line.setItem(item);
            line.setQuantity(qty);
            line.setLineTotal(lineTotal);

            sale.getLines().add(line);
            total += lineTotal;
        }

        sale.setTotal(total);
        return saleRepo.save(sale);
    }
}

