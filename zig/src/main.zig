const std = @import("std");
const mem = std.mem;
const fs = std.fs;
const print = std.debug.print;

// 498,4 -> 498,6 -> 496,6
// 503,4 -> 502,4 -> 502,9 -> 494,9

pub fn main() !void {
    var gpa = std.heap.GeneralPurposeAllocator(.{}){};
    const allocator = gpa.allocator();
    defer _ = gpa.deinit();

    var lines = try readLinesFromFile(allocator, "14.txt");
    for (lines.items) |line| {
        print("{s}\n", .{line});
    }

    freeLines(allocator, &lines);

    // into data structure
    // simulate algorithm
    // tests?
}

// A rule of thumb for returning an ArrayList vs. a slice: If you expect the caller to be adding items 
// the list after calling the function, return an ArrayList. If you expect them to simply look through the list, or simply modify some items in it, return a slice.
fn readLinesFromFile(allocator: std.mem.Allocator, filename: []const u8) !std.ArrayList([]const u8) {
    const buf = try readFromFile(allocator, filename);
    defer allocator.free(buf);

    var lines = try splitString(allocator, buf);
    return lines;
}

fn freeLines(allocator: std.mem.Allocator, lines: *std.ArrayList([]const u8)) void {
    for (lines.items) |line| {
        allocator.free(line);
    }
    lines.clearAndFree();
}

// Returned value has to be free'd by caller.
fn readFromFile(allocator: std.mem.Allocator, fname: []const u8) ![]u8 {
    const fd = try fs.cwd().openFile(fname, .{});
    defer fd.close();
    var size = (try fd.stat()).size;

    var buf: []u8 = allocator.alloc(u8, size) catch {
        _ = std.io.getStdErr().write("Unable to allocate memory to read file") catch {
            // Nothing we can really do here.
        };
        std.os.exit(1);
    };
    _ = try fd.readAll(buf);

    return buf;
}

fn splitString(allocator: std.mem.Allocator, string: []u8) !std.ArrayList([]const u8) {
    var lines = std.ArrayList([]const u8).init(allocator);
    var s: u32 = 0;
    var i: u32 = 0;
    while (i < string.len) : (i += 1) { // for loop?
        if (string[i] != '\n') {
            continue;
        }

        var l = try allocator.alloc(u8, i - s);
        mem.copy(u8, l, string[s..i]);
        try lines.append(l);
        s = i + 1;
        continue;
    }

    var l = try allocator.alloc(u8, i - s);
    mem.copy(u8, l, string[s..i]);
    try lines.append(l);

    return lines;
}