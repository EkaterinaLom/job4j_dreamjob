package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);
    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Ivanov Ivan Jovanovich", "Description Ivanov Ivan Jovanovich",
                LocalDateTime.now(), 2));
        save(new Candidate(0, "Petrov Petr Petrovich", "Description Petrov Petr Petrovich",
                LocalDateTime.now(), 1));
        save(new Candidate(0, "Sidor Denis Abramovich", "Description Sidor Denis Abramovich",
                LocalDateTime.now(), 3));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.getAndIncrement());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(), (id, oldCandidate) -> new Candidate(oldCandidate.getId(),
                candidate.getName(), candidate.getDescription(), candidate.getCreationDate(), candidate.getCityId())) != null;
    }

    @Override
    public Optional<Candidate> findByID(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
