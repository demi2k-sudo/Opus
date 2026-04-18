# Opus Task Engine — Final DB Design + Feature Roadmap

## Current Baseline (Already Implemented)

Existing tables in repo:

- user_table
- zone_table
- user_zone_map_table

These remain the foundation.

---

# Final DB Design

## 1. user_table

- user_id BIGSERIAL PRIMARY KEY
- user_hash VARCHAR(64) UNIQUE NOT NULL
- first_name VARCHAR(100) NOT NULL
- last_name VARCHAR(100)
- email VARCHAR(255) UNIQUE NOT NULL
- password_hash TEXT NOT NULL
- status VARCHAR(20) NOT NULL
- metadata JSONB
- created_at TIMESTAMP NOT NULL
- updated_at TIMESTAMP
- deleted_at TIMESTAMP

---

## 2. zone_table

- zone_id BIGSERIAL PRIMARY KEY
- zone_hash VARCHAR(64) UNIQUE NOT NULL
- zone_code VARCHAR(10) UNIQUE NOT NULL
- zone_name VARCHAR(150) NOT NULL
- description TEXT
- owner_user_id BIGINT NOT NULL REFERENCES user_table(user_id)
- metadata JSONB
- created_at TIMESTAMP NOT NULL
- updated_at TIMESTAMP
- deleted_at TIMESTAMP

---

## 3. user_zone_map_table

- uzm_id BIGSERIAL PRIMARY KEY
- user_id BIGINT NOT NULL REFERENCES user_table(user_id)
- zone_id BIGINT NOT NULL REFERENCES zone_table(zone_id)
- role VARCHAR(50) NOT NULL
- metadata JSONB
- created_at TIMESTAMP NOT NULL
- updated_at TIMESTAMP

---

## 4. task_status_table

- status_id BIGSERIAL PRIMARY KEY
- zone_id BIGINT NOT NULL REFERENCES zone_table(zone_id)
- status_name VARCHAR(50) NOT NULL
- display_order INTEGER NOT NULL
- color VARCHAR(20)
- is_initial BOOLEAN DEFAULT FALSE
- is_final BOOLEAN DEFAULT FALSE
- metadata JSONB
- created_at TIMESTAMP NOT NULL
- updated_at TIMESTAMP

---

## 5. task_priority_table

- priority_id BIGSERIAL PRIMARY KEY
- priority_name VARCHAR(20) NOT NULL
- rank INTEGER NOT NULL
- color VARCHAR(20)
- metadata JSONB

Seed values:
- LOW
- MEDIUM
- HIGH
- CRITICAL

---

## 6. task_type_table

- type_id BIGSERIAL PRIMARY KEY
- type_name VARCHAR(30) NOT NULL
- icon VARCHAR(30)
- metadata JSONB

Seed values:
- TASK
- BUG
- FEATURE
- EPIC

---

## 7. task_table

- task_id BIGSERIAL PRIMARY KEY
- task_key VARCHAR(30) UNIQUE NOT NULL
- zone_id BIGINT NOT NULL REFERENCES zone_table(zone_id)
- title VARCHAR(200) NOT NULL
- description TEXT
- status_id BIGINT NOT NULL REFERENCES task_status_table(status_id)
- priority_id BIGINT NOT NULL REFERENCES task_priority_table(priority_id)
- type_id BIGINT NOT NULL REFERENCES task_type_table(type_id)
- created_by BIGINT NOT NULL REFERENCES user_table(user_id)
- reported_by BIGINT REFERENCES user_table(user_id)
- assigned_to BIGINT REFERENCES user_table(user_id)
- parent_task_id BIGINT REFERENCES task_table(task_id)
- due_at TIMESTAMP
- start_at TIMESTAMP
- completed_at TIMESTAMP
- estimated_minutes INTEGER
- actual_minutes INTEGER
- metadata JSONB
- created_at TIMESTAMP NOT NULL
- updated_at TIMESTAMP
- deleted_at TIMESTAMP

metadata example:
{
  "labels": ["backend", "urgent"],
  "severity": "major",
  "sprint": "S12"
}

---

## 8. task_comment_table

- comment_id BIGSERIAL PRIMARY KEY
- task_id BIGINT NOT NULL REFERENCES task_table(task_id)
- user_id BIGINT NOT NULL REFERENCES user_table(user_id)
- comment_text TEXT NOT NULL
- metadata JSONB
- created_at TIMESTAMP NOT NULL
- updated_at TIMESTAMP
- deleted_at TIMESTAMP

---

## 9. task_attachment_table

- attachment_id BIGSERIAL PRIMARY KEY
- task_id BIGINT NOT NULL REFERENCES task_table(task_id)
- user_id BIGINT NOT NULL REFERENCES user_table(user_id)
- file_name VARCHAR(255) NOT NULL
- file_url TEXT NOT NULL
- mime_type VARCHAR(100)
- size_bytes BIGINT
- metadata JSONB
- created_at TIMESTAMP NOT NULL

---

## 10. task_history_table

- history_id BIGSERIAL PRIMARY KEY
- task_id BIGINT NOT NULL REFERENCES task_table(task_id)
- changed_by BIGINT NOT NULL REFERENCES user_table(user_id)
- field_name VARCHAR(50) NOT NULL
- old_value TEXT
- new_value TEXT
- metadata JSONB
- changed_at TIMESTAMP NOT NULL

---

## 11. task_dependency_table

- dependency_id BIGSERIAL PRIMARY KEY
- task_id BIGINT NOT NULL REFERENCES task_table(task_id)
- depends_on_task_id BIGINT NOT NULL REFERENCES task_table(task_id)
- dependency_type VARCHAR(30) NOT NULL
- metadata JSONB
- created_at TIMESTAMP NOT NULL

Dependency values:
- BLOCKS
- RELATES_TO
- DUPLICATES

---

## 12. task_watcher_table

- twatch_id BIGSERIAL PRIMARY KEY
- task_id BIGINT NOT NULL REFERENCES task_table(task_id)
- user_id BIGINT NOT NULL REFERENCES user_table(user_id)
- metadata JSONB
- created_at TIMESTAMP NOT NULL

---

# Feature Delivery Plan

## Feature 1 — Task Foundation

Scope:
- create task
- fetch task
- update task
- assign task
- soft delete task

Required tables:
- task_table
- task_status_table
- task_priority_table
- task_type_table

Immediate build order:
1. task_priority_table
2. task_type_table
3. task_status_table
4. task_table

---

## Feature 2 — Zone Workflow Management

Scope:
- define statuses per zone
- reorder statuses
- initial/final status rules

Required tables:
- task_status_table

---

## Feature 3 — Task Collaboration

Scope:
- comments
- attachments

Required tables:
- task_comment_table
- task_attachment_table

---

## Feature 4 — Task Audit & Traceability

Scope:
- field change tracking
- assignment tracking
- status history

Required tables:
- task_history_table

---

## Feature 5 — Task Dependency Engine

Scope:
- block task
- relate tasks
- duplicate tasks

Required tables:
- task_dependency_table

---

## Feature 6 — Watchers

Scope:
- follow task updates

Required tables:
- task_watcher_table

---

## Feature 7 — Advanced Query Layer

Scope:
- filter by assignee
- filter by status
- filter by due date
- filter by metadata labels

Required tables:
- no new tables

---

## Feature 8 — Parent/Subtask Engine

Scope:
- subtask create
- nested fetch

Required tables:
- no new tables (uses task_table.parent_task_id)

---

## Feature 9 — Zone Dashboard Metrics

Scope:
- overdue counts
- open counts
- assignee load

Required tables:
- no new tables

---

## Feature 10 — Metadata Intelligence

Scope:
- labels
- severity
- sprint
- custom task fields

Required tables:
- no new tables (uses task_table.metadata)
