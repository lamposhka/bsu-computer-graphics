import cv2
import numpy as np
import tkinter as tk
from tkinter import Tk, Button, filedialog, Canvas
from PIL import Image, ImageTk

class ImageProcessor:
    def __init__(self, master):
        self.master = master
        self.master.title("Image Processing Application")

        # Настройка кнопок
        self.load_button = Button(self.master, text="Load Image", command=self.load_image)
        self.load_button.grid(row=0, column=0, padx=10, pady=10, sticky="ew")

        self.gaussian_button = Button(self.master, text="Gaussian Blur", command=self.gaussian_blur)
        self.gaussian_button.grid(row=1, column=0, padx=10, pady=10, sticky="ew")

        self.median_button = Button(self.master, text="Median Blur", command=self.median_blur)
        self.median_button.grid(row=2, column=0, padx=10, pady=10, sticky="ew")

        self.linear_contrast_button = Button(self.master, text="Linear Contrast", command=self.linear_contrast)
        self.linear_contrast_button.grid(row=3, column=0, padx=10, pady=10, sticky="ew")

        self.histogram_equalization_button = Button(self.master, text="Histogram Equalization",
                                                    command=self.histogram_equalization)
        self.histogram_equalization_button.grid(row=4, column=0, padx=10, pady=10, sticky="ew")

        self.hsv_equalization_button = Button(self.master, text="HSV Histogram Equalization",
                                              command=self.hsv_histogram_equalization)
        self.hsv_equalization_button.grid(row=5, column=0, padx=10, pady=10, sticky="ew")

        self.image = None
        self.filepath = None

        # Canvas для отображения изображений
        self.original_canvas = Canvas(self.master, bg="lightgray", width=380, height=300)
        self.original_canvas.grid(row=0, column=1, rowspan=5, padx=10, pady=10)

        self.processed_canvas = Canvas(self.master, bg="lightgray", width=380, height=300)
        self.processed_canvas.grid(row=0, column=2, rowspan=5, padx=10, pady=10)

    def load_image(self):
        self.filepath = filedialog.askopenfilename()
        self.image = cv2.imread(self.filepath)
        if self.image is not None:
            self.show_image(self.image, self.original_canvas)

    def show_image(self, img, canvas):
        # Изменение размера изображения до размеров канваса
        img_rgb = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        img_pil = Image.fromarray(img_rgb)

        # Получение размеров канваса
        canvas_width = canvas.winfo_width()
        canvas_height = canvas.winfo_height()

        # Изменение размера изображения
        img_pil = img_pil.resize((canvas_width, canvas_height))
        img_tk = ImageTk.PhotoImage(img_pil)

        # Отображение изображения на канвасе
        canvas.create_image(0, 0, anchor=tk.NW, image=img_tk)
        canvas.image = img_tk  # Сохранение ссылки на изображение

    def gaussian_blur(self):
        if self.image is not None:
            filtered_image = cv2.GaussianBlur(self.image, (5, 5), 0)
            self.show_image(filtered_image, self.processed_canvas)

    def median_blur(self):
        if self.image is not None:
            filtered_image = cv2.medianBlur(self.image, 5)
            self.show_image(filtered_image, self.processed_canvas)

    def linear_contrast(self):
        if self.image is not None:
            contrast_image = cv2.normalize(self.image, None, alpha=0, beta=255, norm_type=cv2.NORM_MINMAX)
            self.show_image(contrast_image, self.processed_canvas)

    def histogram_equalization(self):
        if self.image is not None:
            # Разделение изображения на компоненты
            b, g, r = cv2.split(self.image)

            # Выравнивание гистограммы для каждой компоненты
            r_eq = cv2.equalizeHist(r)
            g_eq = cv2.equalizeHist(g)
            b_eq = cv2.equalizeHist(b)

            # Объединение обратно в одно изображение
            equalized_image = cv2.merge((b_eq, g_eq, r_eq))
            self.show_image(equalized_image, self.processed_canvas)

    def hsv_histogram_equalization(self):
        if self.image is not None:
            # Преобразование в HSV
            hsv = cv2.cvtColor(self.image, cv2.COLOR_BGR2HSV)

            # Выделение компоненты яркости (V)
            h, s, v = cv2.split(hsv)

            # Выравнивание гистограммы для компоненты яркости
            v_eq = cv2.equalizeHist(v)

            # Объединение обратно в одно изображение
            hsv_eq = cv2.merge((h, s, v_eq))
            equalized_image = cv2.cvtColor(hsv_eq, cv2.COLOR_HSV2BGR)
            self.show_image(equalized_image, self.processed_canvas)

if __name__ == "__main__":
    root = Tk()
    # Установка фиксированного размера окна
    root.geometry("1000x400")
    # Запрет изменения размера окна
    root.resizable(False, False)
    app = ImageProcessor(root)
    root.mainloop()