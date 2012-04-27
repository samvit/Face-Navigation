CC = g++
CFLAGS =
LDFLAGS = -I/usr/local/include/opencv -lm -lopencv_core -lopencv_highgui -lopencv_video -lopencv_imgproc -framework JavaVM
OBJCFLAGS = -framework ApplicationServices -framework Foundation -framework AppKit
ALL = blink click

all: $(ALL)

blink: blink.o
	$(CC) $(LDFLAGS) -o $@ $^

blink.o: blink.c
	$(CC) $(LDFLAGS) -c $<

click: click.o
	$(CC) $(OBJCFLAGS) -o $@ $^

click.o: click.m
	$(CC) $(OBJCFLAGS) -c $<

.PHONY: clean

clean:
	rm -rf *.o core* $(ALL)