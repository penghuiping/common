package com.php25.common.core.mess.ratelimiter;

import com.php25.common.core.util.AssertUtil;
import com.php25.common.core.util.MathUtil;

import java.util.concurrent.TimeUnit;

/**
 * 限流算法-滑动窗口(非线程安全)
 *
 * @author penghuiping
 * @date 2022/7/18 21:54
 */
public class SlideWindowRateLimiter implements RateLimiter {
    /**
     * 窗口槽计数器
     */
    private final Slot[] slots;

    /**
     * 窗口槽的数量
     */
    private final long slotSize;


    /**
     * 窗口限制
     */
    private final long windowLimit;

    /**
     * 窗口大小，单位（毫秒）
     */
    private final long windowTimeSize;


    /**
     * 窗口开始时间
     */
    private final long startTime;

    /**
     * 槽时间窗口大小
     */
    private final long slotTimeSize;

    /**
     * 一个窗口包含多个窗口槽
     * 窗口槽的数量建议是2的倍数
     *
     * @param slotSize       窗口槽的数量
     * @param windowLimit    窗口限制
     * @param windowTimeSize 窗口大小，单位（纳秒）
     */
    private SlideWindowRateLimiter(long slotSize, long windowLimit, long windowTimeSize) {
        AssertUtil.isTrue(MathUtil.isPowerOfTwo(slotSize), "slotSize必须2的指数幂");
        this.slotSize = slotSize;
        this.slots = new Slot[(int) slotSize];
        this.windowLimit = windowLimit;
        this.windowTimeSize = windowTimeSize;
        this.startTime = System.nanoTime();
        this.slotTimeSize = (windowTimeSize / slotSize);
    }


    private SlideWindowRateLimiter(long windowLimit, long windowTimeSize, TimeUnit windowTimeSizeUnit, long slotsNumber) {
        this(slotsNumber, windowLimit, windowTimeSizeUnit.toNanos(windowTimeSize));
    }

    public SlideWindowRateLimiter(long windowLimit, long windowTimeSize, TimeUnit windowTimeSizeUnit) {
        this(2 << 3, windowLimit, windowTimeSizeUnit.toNanos(windowTimeSize));
    }

    /**
     * 是否允许访问
     *
     * @return true: 是
     */
    @Override
    public Boolean isAllowed() {
        long now = System.nanoTime();
        long numbers = (now - this.startTime) / slotTimeSize;
        long offset = MathUtil.mod(numbers, slotSize);
        long page = numbers / slotSize;
        Slot slot = this.slots[(int) offset];
        if (slot == null) {
            slot = new Slot();
            slot.setPage(page);
            slot.setCount(0);
            slot.setStartTime(this.startTime + page * windowTimeSize + offset * slotTimeSize);
            this.slots[(int) offset] = slot;
        } else {
            if (slot.getPage() != page) {
                slot.setCount(0);
                slot.setPage(page);
            }
            slot.setStartTime(this.startTime + page * windowTimeSize + offset * slotTimeSize);
        }

        if (this.windowTotalCount(now) < this.windowLimit) {
            slot.setCount(slot.getCount() + 1);
            return true;
        }
        return false;
    }

    private long windowTotalCount(long now) {
        long total = 0L;
        for (int i = 0; i < slots.length; i++) {
            Slot slot = slots[i];
            if (null == slot) {
                continue;
            }
            if (slot.getStartTime() >= (now - windowTimeSize) && slot.getStartTime() <= now) {
                total = total + slot.getCount();
            }
        }
        return total;
    }
}
