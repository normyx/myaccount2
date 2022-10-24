package org.mgoulene.mya.service.dto;

import java.time.LocalDate;
import java.util.ListIterator;
import java.util.Stack;
import org.mgoulene.service.dto.OperationDTO;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

public class MyaOperationKey {

    private LocalDate date;
    private int amount;

    private Long accountId;
    private int hash = -1;

    private static StringMetric Metric = StringMetrics.cosineSimilarity();
    private static final float METRIC_THRESHOLD = 0.7f;

    public MyaOperationKey(LocalDate date, Float amount, Long accountId) {
        this.date = date;
        this.amount = amount.intValue();
        this.accountId = accountId;
    }

    public MyaOperationKey(OperationDTO operationDTO) {
        this(operationDTO.getDate(), operationDTO.getAmount(), operationDTO.getAccount().getId());
    }

    @Override
    public int hashCode() {
        if (hash == -1) {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
            result = prime * result + amount;
            result = prime * result + ((date == null) ? 0 : date.hashCode());
            this.hash = result;
        }
        // result = prime * result + ((label == null) ? 0 : label.hashCode());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MyaOperationKey other = (MyaOperationKey) obj;
        if (accountId == null) {
            if (other.accountId != null) return false;
        } else if (!accountId.equals(other.accountId)) return false;
        if (amount != other.amount) return false;
        if (date == null) {
            if (other.date != null) return false;
        } else if (!date.equals(other.date)) return false;
        return true;
    }

    public static boolean same(OperationDTO obj1, OperationDTO obj2) {
        if (obj2 == obj1) return true;
        if (obj1 == null || obj2 == null) return false;

        if (keyEquals(obj1, obj2)) {
            if (obj2.getBankAccount() == null) {
                if (obj1.getBankAccount() != null) return false;
            } else if (!obj2.getBankAccount().equals(obj1.getBankAccount())) return false;
            if (obj2.getCheckNumber() == null) {
                if (obj1.getCheckNumber() != null) return false;
            } else if (!obj2.getCheckNumber().equals(obj1.getCheckNumber())) return false;

            if (obj2.getLabel() == null) {
                if (obj1.getLabel() != null) return false;
            } else if (!obj2.getLabel().equals(obj1.getLabel())) return false;
            if (obj2.getNote() == null) {
                if (obj1.getNote() != null) return false;
            } else if (!obj2.getNote().equals(obj1.getNote())) return false;
            if (obj2.getSubCategory() == null) {
                if (obj1.getSubCategory() != null) return false;
            } else if (!obj2.getSubCategory().equals(obj1.getSubCategory())) return false;
            return true;
        } else return false;
    }

    public static boolean keyEquals(OperationDTO obj1, OperationDTO obj2) {
        if (obj1 == obj2) return true;
        if (obj1 == null || obj2 == null) return false;
        if (obj1.getAccount() != null && obj2.getAccount() != null) {
            if (obj1.getAccount().getId() != null) {
                if (!obj1.getAccount().getId().equals(obj2.getAccount().getId())) return false;
            } else if (obj1.getAccount().getId() != null) return false;
        } else if (obj1.getAccount() == null && obj2.getAccount() == null) return false;
        if (obj1.getAmount() == null) {
            if (obj2.getAmount() != null) return false;
        } else if (obj1.getAmount().intValue() != obj2.getAmount().intValue()) return false;
        if (obj1.getDate() == null) {
            if (obj2.getDate() != null) return false;
        } else if (!obj1.getDate().equals(obj2.getDate())) return false;
        return true;
    }

    public static boolean verySimilar(OperationDTO obj1, OperationDTO obj2) {
        if (keyEquals(obj1, obj2)) {
            if (obj1.getLabel() == null) {
                if (obj2.getLabel() != null) return false;
            } else return Metric.compare(obj1.getLabel(), obj2.getLabel()) > METRIC_THRESHOLD;
        } else return false;
        return true;
    }

    public static OperationSimilarity compare(OperationDTO obj1, OperationDTO obj2) {
        if (keyEquals(obj1, obj2)) {
            if (verySimilar(obj1, obj2)) {
                if (same(obj1, obj2)) return OperationSimilarity.EQUALS; else return OperationSimilarity.VERY_SIMILAR;
            } else return OperationSimilarity.KEY_EQUALS;
        } else return OperationSimilarity.DIFFERENT;
    }

    public static OperationDTO findEquals(OperationDTO op, Stack<OperationDTO> operations) {
        ListIterator<OperationDTO> results = operations.listIterator();
        while (results.hasNext()) {
            OperationDTO op2 = results.next();
            if (same(op, op2)) return op2;
        }
        return null;
    }

    public static OperationDTO findKeyEquals(OperationDTO op, Stack<OperationDTO> operations) {
        ListIterator<OperationDTO> results = operations.listIterator();
        while (results.hasNext()) {
            OperationDTO op2 = results.next();
            if (keyEquals(op, op2)) return op2;
        }
        return null;
    }

    public static OperationDTO findVerySimilar(OperationDTO op, Stack<OperationDTO> operations) {
        ListIterator<OperationDTO> results = operations.listIterator();
        while (results.hasNext()) {
            OperationDTO op2 = results.next();
            if (verySimilar(op, op2)) return op2;
        }
        return null;
    }

    public enum OperationSimilarity {
        EQUALS,
        KEY_EQUALS,
        VERY_SIMILAR,
        DIFFERENT,
    }
}
