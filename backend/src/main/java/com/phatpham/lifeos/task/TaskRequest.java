package com.phatpham.lifeos.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record TaskRequest(
    @NotBlank(message = "Tiêu đề không được để trống.") @Size(max = 200, message = "Tiêu đề tối đa 200 ký tự.") String title,
    @NotNull(message = "Vui lòng chọn ngày thực hiện.") LocalDate scheduledDate
) {}
