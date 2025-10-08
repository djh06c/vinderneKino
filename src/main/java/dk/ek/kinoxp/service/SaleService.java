package dk.ek.kinoxp.service;

import dk.ek.kinoxp.domain.Item;
import dk.ek.kinoxp.domain.Sale;
import dk.ek.kinoxp.domain.SaleLine;
import dk.ek.kinoxp.repository.ItemRepository;
import dk.ek.kinoxp.repository.SaleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class SaleService {
    private final SaleRepository saleRepo;
    private final ItemRepository itemRepo;

    public SaleService(SaleRepository saleRepo, ItemRepository itemRepo) {
        this.saleRepo = saleRepo;
        this.itemRepo = itemRepo;
    }

    public List<Sale> getAll() {
        return saleRepo.findAll(Sort.by(Sort.Direction.DESC, "soldAt"));
    }

    public Sale get(Long id) {
        return saleRepo.findById(id).orElseThrow();
    }

    @Transactional
    public Sale registerSale(Map<Long, Integer> itemQuantities) {
        Sale sale = new Sale();
        int total = 0;

        for (Map.Entry<Long, Integer> entry : itemQuantities.entrySet()) {
            Integer qtyObj = entry.getValue();
            int qty = (qtyObj == null ? 0 : qtyObj);
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

        if (sale.getLines().isEmpty()) {
            throw new IllegalArgumentException("Vælg mindst én vare (antal > 0).");
        }

        sale.setTotal(total);
        return saleRepo.save(sale);
    }
}
