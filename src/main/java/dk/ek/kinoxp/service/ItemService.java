package dk.ek.kinoxp.service;

import dk.ek.kinoxp.domain.Item;
import dk.ek.kinoxp.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository repo;
    public ItemService(ItemRepository repo) { this.repo = repo; }

    public List<Item> getAll() { return repo.findAll(); }
    public Item get(Long id) { return repo.findById(id).orElseThrow(); }
    public Item save(Item item) { return repo.save(item); }
    public void delete(Long id) { repo.deleteById(id); }
}
