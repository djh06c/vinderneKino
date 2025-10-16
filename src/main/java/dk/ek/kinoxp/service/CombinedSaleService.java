package dk.ek.kinoxp.service;

import dk.ek.kinoxp.domain.*;
import dk.ek.kinoxp.domain.enums.TicketType;
import dk.ek.kinoxp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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


        for (Map.Entry<TicketType, Integer> entry : ticketQuantities.entrySet()) {
            int qty = entry.getValue();
            if (qty <= 0) continue;
            for (int i = 0; i < qty; i++) {
                Ticket t = new Ticket();
                t.setScreening(screening);
                t.setType(entry.getKey());
                t.setPrice(screening.getMovie().getPrice());
                t.setSale(sale);
                ticketRepo.save(t);
                total += t.getPrice();
                screening.setAvailableSeats(screening.getAvailableSeats() - 1);
            }
        }
        screeningRepo.save(screening);


        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            int qty = entry.getValue();
            if (qty <= 0) continue;

            Item item = itemRepo.findById(entry.getKey()).orElseThrow();
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
