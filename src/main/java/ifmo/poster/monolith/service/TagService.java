package ifmo.poster.monolith.service;

import ifmo.poster.monolith.entity.Tag;
import ifmo.poster.monolith.exception.ResourceNotFoundException;
import ifmo.poster.monolith.repository.TagRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public Tag getEntity(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found: " + id));
    }

    @Transactional
    public Tag findOrCreateByName(String name) {
        String normalized = name.trim();
        return tagRepository.findByNameIgnoreCase(normalized)
                .orElseGet(() -> {
                    Tag created = new Tag();
                    created.setName(normalized);
                    return tagRepository.save(created);
                });
    }

    @Transactional
    public Set<Tag> resolveTags(Set<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            if (name == null || name.isBlank()) {
                continue;
            }
            tags.add(findOrCreateByName(name));
        }
        return tags;
    }
}
