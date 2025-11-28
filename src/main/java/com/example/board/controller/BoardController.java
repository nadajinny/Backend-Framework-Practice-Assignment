package com.example.board.controller;

import com.example.board.dto.BoardCreateRequest;
import com.example.board.dto.BoardDto;
import com.example.board.dto.BoardUpdateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/boards")
public class BoardController {
    private final Map<Long, BoardDto> store = new HashMap<>();
    private long sequence = 1L;

    private Map<String, Object> res(String status, Object data, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("data", data);
        map.put("message", message);
        return map;
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody BoardCreateRequest request) {
        BoardDto board = new BoardDto();
        board.setId(sequence++);
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());

        store.put(board.getId(), board);
        return ResponseEntity.status(201).body(
                res("success", board, "게시글이 생성되었습니다.")
        );
    }

    @PostMapping("/with-header")
    public ResponseEntity<?> createWithHeader(@RequestBody BoardCreateRequest request, @RequestHeader("X-USER") String user) {
        BoardDto board = new BoardDto();
        board.setId(sequence++);
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        store.put(board.getId(), board);

        return ResponseEntity.status(201).body(
           res("success", board, "작성자(" + user + ")가 글을 생성했습니다.")
        );
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(
                res("success", store.values(), "전체 게시글 조회 성공")
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable long id) {
        BoardDto board = store.get(id);
        if (board == null) {
            return ResponseEntity.status(404).body(
                    res("error", null, "해당 게시글이 존재하지 않습니다.")
            );
        }

        return ResponseEntity.ok(
                res("success", board, "게시글 조회 성공")
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody BoardUpdateRequest request) {
        BoardDto board = store.get(id);
        if (board == null) {
            return ResponseEntity.status(404).body(
                    res("error", null, "수정할 게시글이 없습니다.")
            );
        }

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        return ResponseEntity.ok(
                res("success", board, "게시글 수정 완료")
        );
    }

    @PutMapping("/{id}/title")
    public ResponseEntity<?> updateTitle(@PathVariable long id, @RequestBody Map<String, String> body) {
        BoardDto board = store.get(id);
        if (board == null) {
            return ResponseEntity.status(404).body(
                    res("error", null, "게시글이 존재하지 않습니다.")
            );
        }
        String title = body.get("title");
        if(title == null||title.isBlank()) {
            return ResponseEntity.status(400).body(
                    res("error", null, "잘못된 제목입니다.")
            );
        }

        board.setTitle(title);
        return ResponseEntity.ok(
                res("success", board, "제목 변경 완료")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id) {
        if(!store.containsKey(id)) {
            return ResponseEntity.status(404).body(
                    res("error", null, "삭제할 게시글이 없습니다.")
            );
        }

        store.remove(id);
        return ResponseEntity.status(204).build();
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAll() {
        try{
            store.clear();
            if(true) throw new RuntimeException("서버 내부 예외 발생 테스트");
        }catch (Exception e){
            return ResponseEntity.status(500).body(
                    res("error", null, "서버 오류사 발생했습니다.")
            );
        }

        return ResponseEntity.ok(
                res("success", null, "전체 삭제 완료")
        );
    }
}

