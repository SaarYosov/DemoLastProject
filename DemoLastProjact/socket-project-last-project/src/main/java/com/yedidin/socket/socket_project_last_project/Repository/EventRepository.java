package com.yedidin.socket.socket_project_last_project.Repository;

import com.yedidin.socket.socket_project_last_project.Entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Custom query to find events by user ID
    List<Event> findByUser_Id(Long userId);

    // Custom query to find events by friend ID
    List<Event> findByFriend_Id(Long friendId);

    // Custom query to find events by 'isDone' status
    List<Event> findByIsDone(String isDone);

    // Custom query to find events by priority
    List<Event> findByPriority(String priority);

    // Custom query to find events by user and friend IDs
    List<Event> findByUserId(Long userId);

    List<Event> findByFriendId(Long friendId);

//    Optional<Event> findbyId (Long id);
}


